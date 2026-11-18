package com.wotc.gremio.modelo;

/**
 * Resultado de una tirada de ataque de D&amp;D 5e.
 * Un 20 natural siempre impacta (crítico) y un 1 natural siempre falla (pifia).
 *
 * @param natural       valor del d20
 * @param bonificador   bonificador de ataque sumado al d20
 * @param claseArmadura CA del objetivo
 */
public record TiradaAtaque(int natural, int bonificador, int claseArmadura) {

    public int total() {
        return natural + bonificador;
    }

    public boolean esCritico() {
        return natural == 20;
    }

    public boolean esPifia() {
        return natural == 1;
    }

    public boolean impacta() {
        return !esPifia() && (esCritico() || total() >= claseArmadura);
    }

    @Override
    public String toString() {
        String resultado = esCritico() ? "¡CRÍTICO!" : esPifia() ? "pifia" : impacta() ? "impacto" : "falla";
        return "d20 " + natural + (bonificador >= 0 ? "+" : "") + bonificador + " = " + total()
                + " vs CA " + claseArmadura + ": " + resultado;
    }
}
