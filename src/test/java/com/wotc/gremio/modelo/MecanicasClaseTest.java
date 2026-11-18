package com.wotc.gremio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Cada clase resuelve su turno con su propia mecánica de dados (polimorfismo).
 * Se usan dados fijos para conocer el resultado exacto.
 */
class MecanicasClaseTest {

    private static final Enemigo OGRO = Enemigo.OGRO; // CA 11, salvación +0

    @Test
    void guerreroAtacaDosVecesConOleadaDeAccion() {
        // Nivel 5: ataque +6. d20=5 → 11 vs CA 11 impacta; daño 1d8(5)+3 = 8 por ataque.
        ResultadoTurno r = new Guerrero("Bruenor", 5, 52, true).resolverTurno(new DadosFijos(5), OGRO);

        assertEquals(16, r.danio());
        assertTrue(r.detalle().contains("Ataque 1") && r.detalle().contains("Ataque 2"));
    }

    @Test
    void picaroFallaSiNoSuperaLaArmadura() {
        // d20=4 + 6 = 10 < 11
        ResultadoTurno r = new Picaro("Lyra", 5, 30, true).resolverTurno(new DadosFijos(4), OGRO);

        assertEquals(0, r.danio());
        assertTrue(r.detalle().contains("falla"));
    }

    @Test
    void picaroSumaAtaqueFurtivoSegunSuNivel() {
        // d20=6 → 12 impacta; 1d6(6)+3 = 9; Ataque Furtivo 3d6 (6+6+6) = 18.
        Picaro lyra = new Picaro("Lyra", 5, 30, true);
        ResultadoTurno r = lyra.resolverTurno(new DadosFijos(6), OGRO);

        assertEquals(3, lyra.getDadosAtaqueFurtivo());
        assertEquals(27, r.danio());
        assertEquals(1, new Picaro("Novato", 1, 8, false).getDadosAtaqueFurtivo());
        assertEquals(10, new Picaro("Maestra", 20, 120, false).getDadosAtaqueFurtivo());
    }

    @Test
    void unCriticoDuplicaLosDadosDelExplorador() {
        // d20=20 crítico: flecha 2d8 (1+1) + 3 = 5; Marca del Cazador 2d6 (1+1) = 2.
        ResultadoTurno r = new Explorador("Drizzt", 1, 12, "Bosque")
                .resolverTurno(new DadosFijos(20, 1, 1, 1, 1), OGRO);

        assertEquals(7, r.danio());
        assertTrue(r.detalle().contains("¡CRÍTICO!"));
    }

    @Test
    void magoHaceDanioCompletoSiElEnemigoFallaLaSalvacion() {
        // Bola de Fuego 8d6 (todos 3) = 24; salvación d20=3 + 0 < CD 14.
        Mago elminster = new Mago("Elminster", 5, 30, 4);
        ResultadoTurno r = elminster.resolverTurno(new DadosFijos(3), OGRO);

        assertEquals(14, elminster.getCdSalvacion());
        assertEquals(24, r.danio());
    }

    @Test
    void magoHaceLaMitadSiElEnemigoSuperaLaSalvacion() {
        ResultadoTurno r = new Mago("Elminster", 5, 30, 4)
                .resolverTurno(new DadosFijos(3, 3, 3, 3, 3, 3, 3, 3, 20), OGRO);

        assertEquals(12, r.danio());
        assertTrue(r.detalle().contains("mitad"));
    }

    @Test
    void clerigoCuraCincoPorNivelSinHacerDanio() {
        ResultadoTurno r = new Clerigo("Shandra", 4, 30, "Lathander").resolverTurno(new DadosFijos(10), OGRO);

        assertEquals(0, r.danio());
        assertEquals(20, r.curacion());
        assertTrue(r.detalle().contains("Lathander"));
    }

    @ParameterizedTest
    @CsvSource({"1, 4", "4, 4", "5, 6", "10, 6", "11, 8", "16, 8", "17, 10", "20, 10"})
    void elDadoDeArtesMarcialesCreceConElNivel(int nivel, int caras) {
        assertEquals(caras, Monje.dadoArtesMarciales(nivel));
    }

    @Test
    void monjeGolpeaDosVecesYPuedeAturdir() {
        // Nivel 5: d6. d20=10 → 16 impacta dos veces: (6+3) × 2 = 18; salvación 10 < CD 14.
        ResultadoTurno r = new Monje("Kenji", 5, 38, 5).resolverTurno(new DadosFijos(10), OGRO);

        assertEquals(18, r.danio());
        assertTrue(r.detalle().contains("aturdido"));
    }

    @Test
    void paladinUsaSuFuerzaYCastigoDivino() {
        // Fuerza 18 → +4. Ataque +6: d20=10 impacta. 1d8(8)+4 = 12; Castigo 2d8 = 16.
        Paladin aldric = new Paladin("Aldric", 1, 12, 18);
        ResultadoTurno r = aldric.resolverTurno(new DadosFijos(10), OGRO);

        assertEquals(4, aldric.getModificadorFuerza());
        assertEquals(6, aldric.getBonificadorAtaque());
        assertEquals(28, r.danio());
        assertEquals(-1, new Paladin("Débil", 1, 12, 9).getModificadorFuerza());
    }

    @Test
    void elDanioDeUnImpactoNuncaEsMenorQueUno() {
        // Fuerza 1 → -5. Crítico: 2d8 (1+1) - 5 = -3 → mínimo 1; Castigo 4d8 = 4.
        ResultadoTurno r = new Paladin("Torpe", 1, 12, 1).resolverTurno(new DadosFijos(20, 1, 1, 1, 1, 1, 1), OGRO);

        assertEquals(5, r.danio());
    }

    @ParameterizedTest
    @CsvSource({"1, 6", "4, 6", "5, 8", "9, 8", "10, 10", "14, 10", "15, 12"})
    void elDadoDeInspiracionCreceConElNivel(int nivel, int caras) {
        assertEquals(caras, Bardo.dadoInspiracion(nivel));
    }

    @Test
    void bardoApoyaSinHacerDanio() {
        ResultadoTurno r = new Bardo("Volo", 5, 30, "Laúd").resolverTurno(new DadosFijos(7), OGRO);

        assertEquals(0, r.danio());
        assertTrue(r.detalle().startsWith("Inspiración Bárdica d8 = 7"));
        assertTrue(r.detalle().contains("Laúd"));
    }
}
