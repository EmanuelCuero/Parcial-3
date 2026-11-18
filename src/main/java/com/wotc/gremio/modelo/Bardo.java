package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

public class Bardo extends Aventurero {

    private final String instrumentoMusical;

    public Bardo(String nombre, int nivel, int puntosVida, String instrumentoMusical) {
        super(nombre, nivel, puntosVida);
        this.instrumentoMusical = validar(instrumentoMusical);
    }

    /** Sobrecarga: Bardo novato (nivel 1, vida por defecto). */
    public Bardo(String nombre, String instrumentoMusical) {
        super(nombre);
        this.instrumentoMusical = validar(instrumentoMusical);
    }

    private static String validar(String valor) {
        return Validaciones.texto(valor, "Instrumento musical", Validaciones.LONGITUD_MAXIMA_TEXTO, Campo.ATRIBUTO);
    }

    public String getInstrumentoMusical() {
        return instrumentoMusical;
    }

    @Override
    public String getClase() {
        return "Bardo";
    }

    @Override
    public String getAtributoEspecial() {
        return "Instrumento musical: " + instrumentoMusical;
    }

    @Override
    public String simularCombate() {
        return "El Bardo " + getNombre()
                + " toca su instrumento para otorgar Inspiración Bárdica a un aliado.";
    }

    @Override
    public int getBonificadorIniciativa() {
        return 2;
    }

    /** Dado de Inspiración Bárdica de 5e: d6 (1-4), d8 (5-9), d10 (10-14), d12 (15-20). */
    public static int dadoInspiracion(int nivel) {
        return nivel <= 4 ? 6 : nivel <= 9 ? 8 : nivel <= 14 ? 10 : 12;
    }

    /** Otorga a un aliado un dado de inspiración que suma a su próxima tirada. */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        int caras = dadoInspiracion(getNivel());
        int valor = dados.tirar(caras);
        return new ResultadoTurno("Inspiración Bárdica d" + caras + " = " + valor
                + ": un aliado suma " + valor + " a su próxima tirada (al son de su "
                + instrumentoMusical + ")", 0, 0);
    }
}
