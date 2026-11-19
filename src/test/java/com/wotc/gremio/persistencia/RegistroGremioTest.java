package com.wotc.gremio.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wotc.gremio.modelo.Guerrero;
import com.wotc.gremio.modelo.Mago;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RegistroGremioTest {

    @TempDir
    Path carpeta;

    @Test
    void constructorPorDefectoUsaRegistroGremioTxt() {
        assertEquals(Paths.get("registro_gremio.txt"), new RegistroGremio().getArchivo());
    }

    @Test
    void registrarAventureroCreaElArchivoYAnexaUnaLinea() throws Exception {
        Path archivo = carpeta.resolve("registro_gremio.txt");
        RegistroGremio registro = new RegistroGremio(archivo);

        registro.registrarAventurero(new Guerrero("Bruenor", 5, 52, true));

        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        assertEquals(1, lineas.size());
        assertTrue(lineas.get(0).endsWith(
                "REGISTRO: Guerrero Bruenor | Nivel 5 | PV 52 | Armadura pesada: Sí"));
    }

    @Test
    void registrarExpulsionAnexaUnaLinea() throws Exception {
        Path archivo = carpeta.resolve("registro_gremio.txt");
        new RegistroGremio(archivo).registrarExpulsion(new Mago("Raistlin", 3));

        assertTrue(Files.readString(archivo, StandardCharsets.UTF_8).contains("EXPULSIÓN: Mago Raistlin"));
    }

    @Test
    void anexaSinBorrarLoQueYaHabia() throws Exception {
        Path archivo = carpeta.resolve("registro_gremio.txt");
        Files.writeString(archivo, "historial previo" + System.lineSeparator());
        RegistroGremio registro = new RegistroGremio(archivo);

        registro.registrarAventurero(new Mago("Elminster", 12, 61, 9));
        registro.registrarCombate(List.of("acción 1", "acción 2"));

        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        assertEquals(5, lineas.size());
        assertEquals("historial previo", lineas.get(0));
        assertTrue(lineas.get(1).contains("REGISTRO: Mago Elminster"));
        assertTrue(lineas.get(2).endsWith("SIMULACIÓN DE COMBATE (2 aventureros)"));
        assertEquals("    - acción 1", lineas.get(3));
        assertEquals("    - acción 2", lineas.get(4));
    }

    @Test
    void leerHistorialDevuelveLoQueSeEscribio() throws Exception {
        RegistroGremio registro = new RegistroGremio(carpeta.resolve("registro_gremio.txt"));
        registro.registrarAventurero(new Guerrero("Bruenor", 5, 52, true));
        registro.registrarExpulsion(new Guerrero("Bruenor", 5, 52, true));

        List<String> lineas = registro.leerHistorial();

        assertEquals(2, lineas.size());
        assertTrue(lineas.get(0).contains("REGISTRO: Guerrero Bruenor"));
        assertTrue(lineas.get(1).contains("EXPULSIÓN: Guerrero Bruenor"));
    }

    @Test
    void leerHistorialSinArchivoDevuelveUnaListaVacia() throws Exception {
        assertTrue(new RegistroGremio(carpeta.resolve("vacio.txt")).leerHistorial().isEmpty());
    }

    @Test
    void laRutaDelHistorialNoPuedeSerNula() {
        assertThrows(IllegalArgumentException.class, () -> new RegistroGremio(null));
    }
}
