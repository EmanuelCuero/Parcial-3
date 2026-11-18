package com.wotc.gremio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SimuladorCombateTest {

    @Test
    void actuaPrimeroQuienTieneMasIniciativa() {
        // d20 = 10 para todos: Pícaro +4, Monje +3, Guerrero +1, Clérigo +0.
        List<Aventurero> gremio = List.of(
                new Clerigo("Shandra", 4, 30, "Lathander"),
                new Guerrero("Bruenor", 4, 40, true),
                new Monje("Kenji", 4, 30, 4),
                new Picaro("Lyra", 4, 25, true));

        ResultadoCombate combate = new SimuladorCombate(new DadosFijos(10)).simular(gremio);

        List<String> orden = combate.turnos().stream().map(t -> t.aventurero().getNombre()).toList();
        assertEquals(List.of("Lyra", "Kenji", "Bruenor", "Shandra"), orden);
        assertEquals(14, combate.turnos().get(0).iniciativa());
    }

    @Test
    void enEmpateDeIniciativaVaPrimeroQuienSeRegistroAntes() {
        List<Aventurero> gremio = List.of(new Guerrero("Primero", true), new Guerrero("Segundo", true));

        ResultadoCombate combate = new SimuladorCombate(new DadosFijos(10)).simular(gremio);

        assertEquals("Primero", combate.turnos().get(0).aventurero().getNombre());
    }

    @Test
    void cadaTurnoConservaElMensajeDelRequerimiento() {
        List<Aventurero> gremio = List.of(new Mago("Elminster", 12, 61, 9), new Bardo("Volo", 2, 14, "Laúd"));

        ResultadoCombate combate = new SimuladorCombate(new Dados(7)).simular(gremio);

        for (TurnoCombate turno : combate.turnos()) {
            assertEquals(turno.aventurero().simularCombate(), turno.accion());
        }
        assertEquals(2, combate.turnos().size());
    }

    @Test
    void laVidaDelEnemigoBajaConCadaTurnoYNuncaEsNegativa() {
        // Puros críticos: el Ogro (59 PV) cae y su vida se queda en 0.
        List<Aventurero> gremio = List.of(
                new Guerrero("Bruenor", 3, 30, true), new Picaro("Lyra", 3, 20, true),
                new Explorador("Drizzt", 3, 25, "Bosque"), new Paladin("Aldric", 3, 30, 18));

        ResultadoCombate combate = new SimuladorCombate(new DadosFijos(20)).simular(gremio);

        assertEquals(Enemigo.OGRO, combate.enemigo());
        assertTrue(combate.victoria());
        assertEquals(0, combate.vidaRestante());
        int anterior = Enemigo.OGRO.puntosVida();
        for (TurnoCombate turno : combate.turnos()) {
            assertTrue(turno.vidaEnemigo() <= anterior && turno.vidaEnemigo() >= 0);
            anterior = turno.vidaEnemigo();
        }
        assertTrue(combate.resumen().startsWith("¡Ogro derrotado!"));
    }

    @Test
    void sinDanioSuficienteElEnemigoResiste() {
        List<Aventurero> gremio = List.of(new Bardo("Volo", 1, 8, "Flauta"));

        ResultadoCombate combate = new SimuladorCombate(new DadosFijos(10)).simular(gremio, Enemigo.TROLL);

        assertFalse(combate.victoria());
        assertEquals(84, combate.vidaRestante());
        assertEquals("Troll resiste con 84 de 84 PV tras 0 de daño.", combate.resumen());
    }

    @Test
    void seSumaElDanioYLaCuracionDelGremio() {
        List<Aventurero> gremio = List.of(new Clerigo("Shandra", 4, 30, "Lathander"),
                new Guerrero("Bruenor", 5, 52, true));

        ResultadoCombate combate = new SimuladorCombate(new DadosFijos(5)).simular(gremio, Enemigo.OGRO);

        assertEquals(20, combate.curacionTotal());
        assertEquals(16, combate.danioTotal());
        assertEquals(43, combate.vidaRestante());
    }
}
