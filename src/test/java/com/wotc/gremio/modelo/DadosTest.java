package com.wotc.gremio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DadosTest {

    @RepeatedTest(20)
    void cadaDadoCaeEntreUnoYSusCaras() {
        Dados dados = new Dados();
        for (int caras : new int[]{4, 6, 8, 10, 12, 20}) {
            int valor = dados.tirar(caras);
            assertTrue(valor >= 1 && valor <= caras, "d" + caras + " sacó " + valor);
        }
    }

    @Test
    void variosDadosSumanEntreElMinimoYElMaximo() {
        Dados dados = new Dados();
        for (int i = 0; i < 200; i++) {
            int fuego = dados.tirar(8, 6);
            assertTrue(fuego >= 8 && fuego <= 48);
        }
    }

    @Test
    void laMismaSemillaProduceLasMismasTiradas() {
        Dados a = new Dados(42);
        Dados b = new Dados(42);
        for (int i = 0; i < 50; i++) {
            assertEquals(a.tirar(20), b.tirar(20));
        }
    }

    @Test
    void unDadoNecesitaAlMenosDosCaras() {
        assertThrows(IllegalArgumentException.class, () -> new Dados().tirar(1));
    }

    @Test
    void enUnCriticoSeDuplicanLosDadosDeDanio() {
        assertEquals(2, new DadosFijos(1).tirarDanio(2, 6, false));
        assertEquals(4, new DadosFijos(1).tirarDanio(2, 6, true));
    }

    @Test
    void unVeinteNaturalSiempreImpactaYUnUnoSiempreFalla() {
        TiradaAtaque critico = new TiradaAtaque(20, 0, 99);
        TiradaAtaque pifia = new TiradaAtaque(1, 50, 10);

        assertTrue(critico.esCritico());
        assertTrue(critico.impacta());
        assertTrue(pifia.esPifia());
        assertFalse(pifia.impacta());
    }

    @Test
    void impactaSiIgualaOSuperaLaClaseDeArmadura() {
        assertTrue(new TiradaAtaque(10, 5, 15).impacta());
        assertFalse(new TiradaAtaque(9, 5, 15).impacta());
        assertEquals("d20 10+5 = 15 vs CA 15: impacto", new TiradaAtaque(10, 5, 15).toString());
    }

    @ParameterizedTest
    @CsvSource({"1, 2", "4, 2", "5, 3", "8, 3", "9, 4", "13, 5", "17, 6", "20, 6"})
    void elBonoDeCompetenciaSigueLaTablaDe5e(int nivel, int bono) {
        assertEquals(bono, new Guerrero("Conan", nivel, 50, true).getBonoCompetencia());
    }

    @ParameterizedTest
    @CsvSource({"1.0, Ogro", "3.9, Ogro", "4.0, Troll", "7.5, Troll", "8.0, Dragón Rojo Joven",
        "12.9, Dragón Rojo Joven", "13.0, Dragón Rojo Adulto", "20.0, Dragón Rojo Adulto"})
    void elEnemigoDependeDelNivelPromedio(double nivel, String enemigo) {
        assertEquals(enemigo, Enemigo.segunNivel(nivel).nombre());
    }
}
