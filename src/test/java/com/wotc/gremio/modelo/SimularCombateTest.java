package com.wotc.gremio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Verifica el mensaje exacto (RF3) que produce cada clase al sobrescribir simularCombate().
 */
class SimularCombateTest {

    @Test
    void guerrero() {
        assertEquals("El Guerrero Bruenor usa Oleada de Acción para realizar múltiples ataques físicos continuos.",
                new Guerrero("Bruenor", 5, 52, true).simularCombate());
    }

    @Test
    void mago() {
        assertEquals("El Mago Elminster gasta un espacio de conjuro para lanzar una poderosa Bola de Fuego.",
                new Mago("Elminster", 12, 61, 9).simularCombate());
    }

    @Test
    void clerigo() {
        assertEquals("El Clérigo Shandra usa Canalizar Divinidad para curar a sus aliados y expulsar muertos vivientes.",
                new Clerigo("Shandra", 4, 30, "Lathander").simularCombate());
    }

    @Test
    void monje() {
        assertEquals("El Monje Kenji gasta puntos de Ki para ejecutar una Ráfaga de Golpes y un Ataque Aturdidor.",
                new Monje("Kenji", 6, 41, 6).simularCombate());
    }

    @Test
    void paladin() {
        assertEquals("El Paladín Aldric consume magia para imbuir su arma con un Castigo Divino de daño radiante.",
                new Paladin("Aldric", 8, 70, 18).simularCombate());
    }

    @Test
    void explorador() {
        assertEquals("El Explorador Drizzt aplica Marca del Cazador sobre el objetivo para maximizar su daño sostenido.",
                new Explorador("Drizzt", 10, 80, "Underdark").simularCombate());
    }

    @Test
    void picaro() {
        assertEquals("El Pícaro Lyra aprovecha la ventaja táctica para asestar un Ataque Furtivo masivo y se reposiciona.",
                new Picaro("Lyra", 3, 21, true).simularCombate());
    }

    @Test
    void bardo() {
        assertEquals("El Bardo Volo toca su instrumento para otorgar Inspiración Bárdica a un aliado.",
                new Bardo("Volo", 2, 14, "Laúd").simularCombate());
    }

    @Test
    void elMetodoSeResuelveSegunElTipoReal() {
        IAccionCombate accion = new Mago("Raistlin", 3);
        assertEquals("El Mago Raistlin gasta un espacio de conjuro para lanzar una poderosa Bola de Fuego.",
                accion.simularCombate());
    }
}
