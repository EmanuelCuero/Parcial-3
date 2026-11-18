package com.wotc.gremio.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wotc.gremio.excepciones.AventureroDuplicadoException;
import com.wotc.gremio.excepciones.HistorialException;
import com.wotc.gremio.modelo.Aventurero;
import com.wotc.gremio.modelo.Bardo;
import com.wotc.gremio.modelo.Monje;
import com.wotc.gremio.modelo.Paladin;
import com.wotc.gremio.modelo.TurnoCombate;
import com.wotc.gremio.modelo.Clerigo;
import com.wotc.gremio.modelo.Guerrero;
import com.wotc.gremio.modelo.DadosFijos;
import com.wotc.gremio.modelo.Mago;
import com.wotc.gremio.modelo.ResultadoCombate;
import com.wotc.gremio.modelo.SimuladorCombate;
import com.wotc.gremio.persistencia.RegistroGremio;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Prueba la lógica del controlador sin la interfaz gráfica.
 */
class GremioControllerTest {

    @TempDir
    Path carpeta;

    private Path archivo;
    private GremioController controlador;

    @BeforeEach
    void preparar() {
        archivo = carpeta.resolve("registro_gremio.txt");
        // Dados fijos (d20 = 10) para que la iniciativa sea predecible.
        controlador = new GremioController(new RegistroGremio(archivo),
                new SimuladorCombate(new DadosFijos(10)));
    }

    @Test
    void registrarGuardaEnLaColeccionYEnElArchivo() throws Exception {
        controlador.registrarAventurero(new Guerrero("Bruenor", 5, 52, true));
        controlador.registrarAventurero(new Mago("Elminster", 12, 61, 9));

        assertEquals(2, controlador.getAventureros().size());
        assertEquals(2, Files.readAllLines(archivo, StandardCharsets.UTF_8).size());
    }

    @Test
    void laColeccionNoSePuedeModificarDesdeFuera() {
        assertThrows(UnsupportedOperationException.class,
                () -> controlador.getAventureros().add(new Bardo("Volo", "Laúd")));
    }

    @Test
    void noSeAceptaUnAventureroNulo() {
        assertThrows(IllegalArgumentException.class, () -> controlador.registrarAventurero(null));
    }

    @Test
    void simularCombateEsPolimorficoYSigueLaIniciativa() throws Exception {
        controlador.registrarAventurero(new Guerrero("Bruenor", 5, 52, true));
        controlador.registrarAventurero(new Clerigo("Shandra", 4, 30, "Lathander"));
        controlador.registrarAventurero(new Bardo("Volo", 2, 14, "Laúd"));

        List<String> acciones = controlador.simularCombate();

        // Iniciativa con d20 = 10: Bardo 12, Guerrero 11, Clérigo 10.
        assertEquals(3, acciones.size());
        assertTrue(acciones.get(0).startsWith("El Bardo Volo toca su instrumento"));
        assertTrue(acciones.get(1).startsWith("El Guerrero Bruenor usa Oleada de Acción"));
        assertTrue(acciones.get(2).startsWith("El Clérigo Shandra usa Canalizar Divinidad"));
    }

    @Test
    void laBatallaEligeEnemigoSegunElNivelDelGremio() throws Exception {
        controlador.registrarAventurero(new Mago("Elminster", 12, 61, 9));
        controlador.registrarAventurero(new Guerrero("Bruenor", 10, 90, true));

        ResultadoCombate combate = controlador.simularBatalla();

        assertEquals("Dragón Rojo Joven", combate.enemigo().nombre());
        assertEquals(2, combate.turnos().size());
    }

    @Test
    void noSePuedeLanzarUnaBatallaSinAventureros() {
        assertThrows(IllegalStateException.class, () -> controlador.simularBatalla());
    }

    @Test
    void simularCombateAnexaElResultadoAlHistorial() throws Exception {
        controlador.registrarAventurero(new Mago("Elminster", 12, 61, 9));
        controlador.simularCombate();

        String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
        assertTrue(contenido.contains("SIMULACIÓN DE COMBATE (1 aventureros)"));
        assertTrue(contenido.contains("] El Mago Elminster gasta un espacio de conjuro"));
        assertTrue(contenido.contains("contra Dragón Rojo Joven (CA 18, 178 PV)"));
        assertTrue(contenido.contains("Tiradas: Bola de Fuego 8d6"));
        assertTrue(contenido.contains("RESULTADO: Dragón Rojo Joven resiste"));
    }

    @Test
    void simularSinAventurerosNoEscribeNada() throws Exception {
        assertTrue(controlador.simularCombate().isEmpty());
        assertFalse(Files.exists(archivo));
    }

    @Test
    void noSePermitenNombresRepetidosSinImportarMayusculas() throws Exception {
        controlador.registrarAventurero(new Bardo("Volo", 2, 14, "Laúd"));

        AventureroDuplicadoException error = assertThrows(AventureroDuplicadoException.class,
                () -> controlador.registrarAventurero(new Mago("VOLO", 3)));
        assertEquals("VOLO", error.getNombre());
        assertEquals(1, controlador.getAventureros().size());
        assertTrue(controlador.existeAventurero(" volo "));
    }

    @Test
    void expulsarQuitaDeLaColeccionYQuedaEnElHistorial() throws Exception {
        controlador.registrarAventurero(new Guerrero("Bruenor", 5, 52, true));
        controlador.registrarAventurero(new Mago("Elminster", 12, 61, 9));

        Aventurero expulsado = controlador.expulsarAventurero(0);

        assertEquals("Bruenor", expulsado.getNombre());
        assertEquals(1, controlador.getAventureros().size());
        assertTrue(Files.readString(archivo, StandardCharsets.UTF_8)
                .contains("EXPULSIÓN: Guerrero Bruenor"));
        assertThrows(IllegalArgumentException.class, () -> controlador.expulsarAventurero(5));
    }

    @Test
    void lasEstadisticasResumenAlGremio() throws Exception {
        assertEquals(0, controlador.getNivelPromedio());
        controlador.registrarAventurero(new Guerrero("Bruenor", 5, 52, true));
        controlador.registrarAventurero(new Mago("Elminster", 12, 61, 9));
        controlador.registrarAventurero(new Clerigo("Shandra", 4, 30, "Lathander"));

        assertEquals(7.0, controlador.getNivelPromedio(), 0.001);
        assertEquals(143, controlador.getPuntosVidaTotales());
    }

    @Test
    void losTurnosSeNumeranEnOrdenYGuardanAlAventurero() throws Exception {
        controlador.registrarAventurero(new Monje("Kenji", 6, 41, 6));
        controlador.registrarAventurero(new Paladin("Aldric", 8, 70, 18));

        List<TurnoCombate> turnos = controlador.simularTurnos();

        assertEquals(1, turnos.get(0).numero());
        assertEquals(2, turnos.get(1).numero());
        assertEquals("Aldric", turnos.get(1).aventurero().getNombre());
        assertEquals(turnos.get(1).aventurero().simularCombate(), turnos.get(1).accion());
    }

    @Test
    void siElHistorialFallaSeLanzaHistorialExceptionYNoSeRegistra() throws Exception {
        // Una carpeta en lugar de un archivo provoca un error de escritura.
        Path carpetaComoArchivo = Files.createDirectory(carpeta.resolve("bloqueado"));
        GremioController conFallo = new GremioController(new RegistroGremio(carpetaComoArchivo));

        HistorialException error = assertThrows(HistorialException.class,
                () -> conFallo.registrarAventurero(new Guerrero("Bruenor", true)));
        assertTrue(error.getMessage().startsWith("No se pudo guardar el registro en el historial"));
        assertTrue(conFallo.getAventureros().isEmpty());
    }
}
