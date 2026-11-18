package com.wotc.gremio.modelo;

/**
 * Dados "trucados" para las pruebas: devuelven los valores indicados en orden
 * (y vuelven a empezar), recortados al número de caras del dado.
 */
public class DadosFijos extends Dados {

    private final int[] valores;
    private int posicion;

    public DadosFijos(int... valores) {
        super(0);
        this.valores = valores;
    }

    @Override
    public int tirar(int caras) {
        int valor = valores[posicion++ % valores.length];
        return Math.max(1, Math.min(valor, caras));
    }
}
