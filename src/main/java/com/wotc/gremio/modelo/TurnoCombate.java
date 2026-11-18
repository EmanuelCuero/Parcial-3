package com.wotc.gremio.modelo;

/**
 * Un turno de la simulación: quién actuó, qué hizo y cómo le fue con los dados.
 *
 * @param numero          posición del turno (empieza en 1)
 * @param aventurero      quien ejecuta la acción
 * @param accion          mensaje devuelto por {@link IAccionCombate#simularCombate()}
 * @param iniciativa      tirada de iniciativa (d20 + bonificador)
 * @param resultado       tiradas, daño y curación del turno
 * @param vidaEnemigo     puntos de vida que le quedan al enemigo tras el turno
 */
public record TurnoCombate(int numero, Aventurero aventurero, String accion, int iniciativa,
        ResultadoTurno resultado, int vidaEnemigo) {
}
