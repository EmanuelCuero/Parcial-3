package com.wotc.gremio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AventureroTest {

    @Test
    void constructorCompletoGuardaLosDatosBasicos() {
        Aventurero mago = new Mago("Elminster", 12, 61, 9);

        assertEquals("Elminster", mago.getNombre());
        assertEquals(12, mago.getNivel());
        assertEquals(61, mago.getPuntosVida());
        assertEquals("Mago", mago.getClase());
    }

    @Test
    void constructorSobrecargadoCreaUnNovato() {
        Guerrero guerrero = new Guerrero("Bruenor", true);

        assertEquals(Aventurero.NIVEL_POR_DEFECTO, guerrero.getNivel());
        assertEquals(Aventurero.PUNTOS_VIDA_POR_DEFECTO, guerrero.getPuntosVida());
        assertTrue(guerrero.isArmaduraPesada());
    }

    @Test
    void elNombreSeRecortaYEsObligatorio() {
        assertEquals("Lyra", new Picaro("  Lyra  ", false).getNombre());
        assertThrows(IllegalArgumentException.class, () -> new Picaro("   ", 3, 20, true));
        assertThrows(IllegalArgumentException.class, () -> new Picaro(null, 3, 20, true));
    }

    @Test
    void elNivelDebeEstarEntre1Y20() {
        assertThrows(IllegalArgumentException.class, () -> new Monje("Kenji", 0, 30, 2));
        assertThrows(IllegalArgumentException.class, () -> new Monje("Kenji", 21, 30, 2));
        assertEquals(20, new Monje("Kenji", 20, 30, 2).getNivel());
    }

    @Test
    void losPuntosDeVidaDebenSerPositivos() {
        assertThrows(IllegalArgumentException.class, () -> new Bardo("Volo", 2, 0, "Laúd"));
    }

    @Test
    void losAtributosEspecialesSeValidan() {
        assertThrows(IllegalArgumentException.class, () -> new Mago("Raistlin", -1));
        assertThrows(IllegalArgumentException.class, () -> new Monje("Kenji", -2));
        assertThrows(IllegalArgumentException.class, () -> new Paladin("Aldric", -5));
        assertThrows(IllegalArgumentException.class, () -> new Clerigo("Shandra", " "));
        assertThrows(IllegalArgumentException.class, () -> new Explorador("Drizzt", ""));
        assertThrows(IllegalArgumentException.class, () -> new Bardo("Volo", null));
    }

    @Test
    void describirTieneSobrecargaBreveYDetallada() {
        Aventurero clerigo = new Clerigo("Shandra", 4, 30, "Lathander");

        assertEquals("Clérigo Shandra", clerigo.describir());
        assertEquals("Clérigo Shandra", clerigo.describir(false));
        assertEquals("Clérigo Shandra | Nivel 4 | PV 30 | Dios venerado: Lathander",
                clerigo.describir(true));
    }

    @Test
    void todasLasClasesSonAventurerosConAccionDeCombate() {
        Aventurero explorador = new Explorador("Drizzt", 10, 80, "Underdark");

        assertInstanceOf(IAccionCombate.class, explorador);
        assertEquals("Terreno favorecido: Underdark", explorador.getAtributoEspecial());
    }
}
