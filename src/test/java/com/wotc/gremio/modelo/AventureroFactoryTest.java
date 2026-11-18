package com.wotc.gremio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AventureroFactoryTest {

    @Test
    void creaClasesConAtributoBooleano() {
        Aventurero picaro = AventureroFactory.crear(ClaseAventurero.PICARO, "Lyra", 3, 21, false);

        assertInstanceOf(Picaro.class, picaro);
        assertFalse(((Picaro) picaro).isHerramientasLadron());
        assertInstanceOf(Guerrero.class,
                AventureroFactory.crear(ClaseAventurero.GUERRERO, "Bruenor", 5, 52, true));
    }

    @Test
    void creaClasesConAtributoEntero() {
        Aventurero monje = AventureroFactory.crear(ClaseAventurero.MONJE, "Kenji", 6, 41, 6);

        assertInstanceOf(Monje.class, monje);
        assertEquals(6, ((Monje) monje).getPuntosKi());
    }

    @Test
    void convierteTextoAEnteroParaClasesNumericas() {
        Aventurero paladin = AventureroFactory.crear(ClaseAventurero.PALADIN, "Aldric", 8, 70, " 18 ");

        assertInstanceOf(Paladin.class, paladin);
        assertEquals(18, ((Paladin) paladin).getPuntosFuerza());
    }

    @Test
    void creaClasesConAtributoDeTexto() {
        Aventurero bardo = AventureroFactory.crear(ClaseAventurero.BARDO, "Volo", 2, 14, "Laúd");

        assertInstanceOf(Bardo.class, bardo);
        assertEquals("Laúd", ((Bardo) bardo).getInstrumentoMusical());
    }

    @Test
    void rechazaTextoNoNumericoParaClasesNumericas() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> AventureroFactory.crear(ClaseAventurero.MAGO, "Elminster", 12, 61, "nueve"));
        assertEquals("Espacios de conjuro: debe ser un número entero.", error.getMessage());
    }

    @Test
    void rechazaUnTipoDeAtributoQueNoCorrespondeALaClase() {
        assertThrows(IllegalArgumentException.class,
                () -> AventureroFactory.crear(ClaseAventurero.MAGO, "Elminster", 12, 61, true));
        assertThrows(IllegalArgumentException.class,
                () -> AventureroFactory.crear(ClaseAventurero.BARDO, "Volo", 2, 14, 3));
        assertThrows(IllegalArgumentException.class,
                () -> AventureroFactory.crear(ClaseAventurero.GUERRERO, "Bruenor", 5, 52, "sí"));
    }

    @Test
    void cadaClaseTieneSuTipoDeAtributo() {
        assertEquals(ClaseAventurero.TipoAtributo.BOOLEANO, ClaseAventurero.GUERRERO.getTipoAtributo());
        assertEquals(ClaseAventurero.TipoAtributo.ENTERO, ClaseAventurero.MAGO.getTipoAtributo());
        assertEquals(ClaseAventurero.TipoAtributo.TEXTO, ClaseAventurero.CLERIGO.getTipoAtributo());
        assertEquals(8, ClaseAventurero.values().length);
    }
}
