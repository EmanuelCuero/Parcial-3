package com.wotc.gremio.modelo;

import java.util.List;

/**
 * Resultado completo de una simulación de combate contra un enemigo.
 */
public record ResultadoCombate(Enemigo enemigo, List<TurnoCombate> turnos) {

    public ResultadoCombate {
        turnos = List.copyOf(turnos);
    }

    public int danioTotal() {
        return turnos.stream().mapToInt(t -> t.resultado().danio()).sum();
    }

    public int curacionTotal() {
        return turnos.stream().mapToInt(t -> t.resultado().curacion()).sum();
    }

    public int vidaRestante() {
        return Math.max(0, enemigo.puntosVida() - danioTotal());
    }

    public boolean victoria() {
        return vidaRestante() == 0;
    }

    /** Resumen en una línea, por ejemplo para la barra de estado y el historial. */
    public String resumen() {
        return victoria()
                ? "¡" + enemigo.nombre() + " derrotado! El gremio causó " + danioTotal() + " de daño."
                : enemigo.nombre() + " resiste con " + vidaRestante() + " de " + enemigo.puntosVida()
                + " PV tras " + danioTotal() + " de daño.";
    }
}
