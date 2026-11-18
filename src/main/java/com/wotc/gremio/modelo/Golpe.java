package com.wotc.gremio.modelo;

/**
 * Un ataque con arma o desarmado: la tirada y el daño causado (0 si falla).
 */
public record Golpe(TiradaAtaque tirada, int danio) {

    public boolean impacta() {
        return tirada.impacta();
    }

    public String describir(String etiqueta, String tipoDanio) {
        return etiqueta + ": " + tirada + (impacta() ? " → " + danio + " " + tipoDanio : "");
    }
}
