package com.wotc.gremio.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wotc.gremio.excepciones.FormatoArchivoException;
import com.wotc.gremio.modelo.Aventurero;
import com.wotc.gremio.modelo.Bardo;
import com.wotc.gremio.modelo.Clerigo;
import com.wotc.gremio.modelo.Explorador;
import com.wotc.gremio.modelo.Guerrero;
import com.wotc.gremio.modelo.Mago;
import com.wotc.gremio.modelo.Monje;
import com.wotc.gremio.modelo.Paladin;
import com.wotc.gremio.modelo.Picaro;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Guardar y volver a cargar el gremio desde el archivo de texto.
 */
class ArchivoGremioTest {

    @TempDir
    Path carpeta;

    private ArchivoGremio archivoEn(String nombre) {
        return new ArchivoGremio(carpeta.resolve(nombre));
    }

    private void escribir(ArchivoGremio archivo, String... lineas) throws IOException {
        Files.write(archivo.getArchivo(), List.of(lineas), StandardCharsets.UTF_8);
    }

    @Test
    void constructorPorDefectoUsaGremioTxt() {
        assertEquals(Paths.get("gremio.txt"), new ArchivoGremio().getArchivo());
    }

    @Test
    void laRutaNoPuedeSerNula() {
        assertThrows(IllegalArgumentException.class, () -> new ArchivoGremio(null));
    }

    @Test
    void guardarYCargarConservaLasOchoClasesConSusAtributos() throws Exception {
        ArchivoGremio archivo = archivoEn("gremio.txt");
        List<Aventurero> originales = List.of(
                new Guerrero("Bruenor", 5, 52, true),
                new Mago("Elminster", 12, 61, 9),
                new Clerigo("Shandra", 4, 30, "Lathander"),
                new Monje("Kenji", 5, 38, 5),
                new Paladin("Aldric", 1, 12, 18),
                new Explorador("Drizzt", 7, 60, "Bosque"),
                new Picaro("Lyra", 3, 20, false),
                new Bardo("Volo", 5, 30, "Laúd"));

        archivo.guardar(originales);
        List<Aventurero> cargados = archivo.cargar();

        assertEquals(originales.size(), cargados.size());
        for (int i = 0; i < originales.size(); i++) {
            Aventurero esperado = originales.get(i);
            Aventurero leido = cargados.get(i);
            assertEquals(esperado.getClass(), leido.getClass());
            assertEquals(esperado.describir(true), leido.describir(true));
        }
    }

    @Test
    void cargarSinArchivoDevuelveUnaListaVacia() throws Exception {
        assertTrue(archivoEn("no-existe.txt").cargar().isEmpty());
    }

    @Test
    void guardarReemplazaElContenidoAnterior() throws Exception {
        ArchivoGremio archivo = archivoEn("gremio.txt");
        archivo.guardar(List.of(new Mago("Elminster", 12, 61, 9), new Mago("Raistlin", 3, 15, 2)));

        archivo.guardar(List.of(new Bardo("Volo", 5, 30, "Laúd")));

        List<Aventurero> cargados = archivo.cargar();
        assertEquals(1, cargados.size());
        assertEquals("Volo", cargados.get(0).getNombre());
    }

    @Test
    void guardarUnaListaVaciaDejaElGremioVacio() throws Exception {
        ArchivoGremio archivo = archivoEn("gremio.txt");
        archivo.guardar(List.of());

        assertTrue(Files.exists(archivo.getArchivo()));
        assertTrue(archivo.cargar().isEmpty());
    }

    @Test
    void ignoraLineasVaciasYComentarios() throws Exception {
        ArchivoGremio archivo = archivoEn("gremio.txt");
        escribir(archivo, "# comentario", "", "   ", "Guerrero;Bruenor;5;52;true");

        List<Aventurero> cargados = archivo.cargar();

        assertEquals(1, cargados.size());
        assertInstanceOf(Guerrero.class, cargados.get(0));
    }

    @Test
    void elFormatoDeCadaLineaEsLegible() {
        assertEquals("Mago;Elminster;12;61;9", ArchivoGremio.aLinea(new Mago("Elminster", 12, 61, 9)));
        assertEquals("Pícaro;Lyra;3;20;false", ArchivoGremio.aLinea(new Picaro("Lyra", 3, 20, false)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Guerrero;Bruenor;5;52",              // faltan campos
        "Guerrero;Bruenor;5;52;true;extra",   // sobran campos
        "Hechicero;Merlín;5;30;Fuego",        // clase desconocida
        "Mago;Elminster;doce;61;9",           // nivel no numérico
        "Mago;Elminster;12;61;muchos",        // atributo no numérico
        "Guerrero;Bruenor;5;52;quizas",       // booleano inválido
        "Mago;Elminster;99;61;9",             // nivel fuera de rango
        "Monje;Kenji;2;20;9",                 // más Ki que nivel
        "Clérigo;Shandra;4;30;",              // atributo vacío
        "Bardo;X;5;30;Laúd"                   // nombre demasiado corto
    })
    void unaLineaMalFormadaLanzaFormatoArchivoException(String linea) throws IOException {
        ArchivoGremio archivo = archivoEn("gremio.txt");
        escribir(archivo, "Guerrero;Bruenor;5;52;true", linea);

        FormatoArchivoException ex = assertThrows(FormatoArchivoException.class, archivo::cargar);

        assertEquals(2, ex.getNumeroLinea());
        assertTrue(ex.getMessage().startsWith("Línea 2"));
    }

    @Test
    void laClaseSeReconoceSinImportarMayusculas() throws Exception {
        ArchivoGremio archivo = archivoEn("gremio.txt");
        escribir(archivo, "CLÉRIGO;Shandra;4;30;Lathander");

        assertFalse(archivo.cargar().isEmpty());
    }

    @Test
    void siLaRutaEsUnaCarpetaGuardarLanzaIOException() {
        ArchivoGremio archivo = new ArchivoGremio(carpeta);

        assertThrows(IOException.class, () -> archivo.guardar(List.of(new Mago("Elminster", 12, 61, 9))));
    }
}
