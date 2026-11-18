package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

public class Clerigo extends Aventurero {

    private final String diosVenerado;

    public Clerigo(String nombre, int nivel, int puntosVida, String diosVenerado) {
        super(nombre, nivel, puntosVida);
        this.diosVenerado = validar(diosVenerado);
    }

    /** Sobrecarga: Clérigo novato (nivel 1, vida por defecto). */
    public Clerigo(String nombre, String diosVenerado) {
        super(nombre);
        this.diosVenerado = validar(diosVenerado);
    }

    private static String validar(String valor) {
        return Validaciones.texto(valor, "Dios venerado", Validaciones.LONGITUD_MAXIMA_TEXTO, Campo.ATRIBUTO);
    }

    public String getDiosVenerado() {
        return diosVenerado;
    }

    @Override
    public String getClase() {
        return "Clérigo";
    }

    @Override
    public String getAtributoEspecial() {
        return "Dios venerado: " + diosVenerado;
    }

    @Override
    public String simularCombate() {
        return "El Clérigo " + getNombre()
                + " usa Canalizar Divinidad para curar a sus aliados y expulsar muertos vivientes.";
    }

    /** Canalizar Divinidad (Preservar Vida): restaura 5 × nivel PV entre sus aliados. */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        int curacion = 5 * getNivel();
        return new ResultadoTurno("Preservar Vida: restaura " + curacion
                + " PV a sus aliados (" + diosVenerado + " lo guía)", 0, curacion);
    }
}
