package com.wotc.gremio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wotc.gremio.excepciones.DatosInvalidosException;
import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidacionesTest {

    @Test
    void textoValidoSeNormalizaQuitandoEspaciosSobrantes() {
        assertEquals("Mary Jane", Validaciones.texto("  Mary    Jane ", "Nombre", 30, Campo.NOMBRE));
        assertEquals("D'Artagnan", Validaciones.texto("D'Artagnan", "Nombre", 30, Campo.NOMBRE));
        assertEquals("Ñandú-Pérez", Validaciones.texto("Ñandú-Pérez", "Nombre", 30, Campo.NOMBRE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"R2D2", "Ana123", "@lex", "-Juan", "Hola!"})
    void textoConNumerosOSimbolosEsRechazado(String nombre) {
        DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
                () -> Validaciones.texto(nombre, "Nombre", 30, Campo.NOMBRE));
        assertEquals(Campo.NOMBRE, error.getCampo());
    }

    @Test
    void textoDemasiadoCortoOLargoEsRechazado() {
        assertThrows(DatosInvalidosException.class,
                () -> Validaciones.texto("A", "Nombre", 30, Campo.NOMBRE));
        assertThrows(DatosInvalidosException.class,
                () -> Validaciones.texto("A".repeat(31), "Nombre", 30, Campo.NOMBRE));
    }

    @Test
    void textoVacioIndicaQueEsObligatorio() {
        DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
                () -> Validaciones.texto("   ", "Dios venerado", 40, Campo.ATRIBUTO));
        assertEquals("Dios venerado: es obligatorio.", error.getMessage());
        assertEquals(Campo.ATRIBUTO, error.getCampo());
    }

    @Test
    void rangoAceptaLosExtremosYRechazaLoDemas() {
        assertEquals(1, Validaciones.rango(1, 1, 20, "Nivel", Campo.NIVEL));
        assertEquals(20, Validaciones.rango(20, 1, 20, "Nivel", Campo.NIVEL));
        DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
                () -> Validaciones.rango(21, 1, 20, "Nivel", Campo.NIVEL));
        assertEquals("Nivel: debe estar entre 1 y 20.", error.getMessage());
    }

    @Test
    void cadaErrorDelAventureroIndicaSuCampo() {
        assertEquals(Campo.NOMBRE, assertThrows(DatosInvalidosException.class,
                () -> new Guerrero("X1", 1, 10, true)).getCampo());
        assertEquals(Campo.NIVEL, assertThrows(DatosInvalidosException.class,
                () -> new Guerrero("Conan", 25, 10, true)).getCampo());
        assertEquals(Campo.PUNTOS_VIDA, assertThrows(DatosInvalidosException.class,
                () -> new Guerrero("Conan", 5, 1000, true)).getCampo());
        assertEquals(Campo.ATRIBUTO, assertThrows(DatosInvalidosException.class,
                () -> new Mago("Merlin", 5, 30, 31)).getCampo());
    }

    @Test
    void unMonjeNoPuedeTenerMasKiQueSuNivel() {
        assertEquals(6, new Monje("Kenji", 6, 41, 6).getPuntosKi());
        DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
                () -> new Monje("Lin", 3, 20, 9));
        assertEquals("Puntos de Ki: un Monje de nivel 3 tiene como máximo 3 puntos de Ki.",
                error.getMessage());
    }

    @Test
    void laFuerzaDelPaladinEsUnaPuntuacionDe1A30() {
        assertEquals(30, new Paladin("Aldric", 8, 70, 30).getPuntosFuerza());
        assertThrows(DatosInvalidosException.class, () -> new Paladin("Aldric", 8, 70, 0));
        assertThrows(DatosInvalidosException.class, () -> new Paladin("Aldric", 8, 70, 31));
    }

    @Test
    void elAtributoDeTextoSoloAdmiteLetras() {
        assertThrows(DatosInvalidosException.class, () -> new Bardo("Volo", "Laúd 2000"));
        assertEquals("Flauta de Pan", new Bardo("Volo", "Flauta de Pan").getInstrumentoMusical());
    }

    @Test
    void laFabricaIndicaCuandoFaltaElAtributoNumerico() {
        DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
                () -> AventureroFactory.crear(ClaseAventurero.MONJE, "Kenji", 6, 41, " "));
        assertEquals("Puntos de Ki: es obligatorio.", error.getMessage());
    }

    @Test
    void laClaseSeBuscaPorSuNombreVisible() {
        assertEquals(ClaseAventurero.CLERIGO, ClaseAventurero.desdeNombre("Clérigo").orElseThrow());
        assertEquals(ClaseAventurero.PICARO, ClaseAventurero.desdeNombre("pícaro").orElseThrow());
        assertEquals(true, ClaseAventurero.desdeNombre("Bárbaro").isEmpty());
    }
}
