package com.wotc.gremio.modelo;

/**
 * Lo que logró un aventurero en su turno.
 *
 * @param detalle  descripción de las tiradas
 * @param danio    daño causado al enemigo
 * @param curacion puntos de vida restaurados a los aliados
 */
public record ResultadoTurno(String detalle, int danio, int curacion) {

    public ResultadoTurno {
        if (danio < 0 || curacion < 0) {
            throw new IllegalArgumentException("El daño y la curación no pueden ser negativos.");
        }
    }
}
