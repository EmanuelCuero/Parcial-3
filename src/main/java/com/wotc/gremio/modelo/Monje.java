package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException;
import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

public class Monje extends Aventurero {

    public static final int MINIMO = 0;
    public static final int MAXIMO = 20;

    private final int puntosKi;

    public Monje(String nombre, int nivel, int puntosVida, int puntosKi) {
        super(nombre, nivel, puntosVida);
        this.puntosKi = validar(puntosKi);
    }

    /** Sobrecarga: Monje novato (nivel 1, vida por defecto). */
    public Monje(String nombre, int puntosKi) {
        super(nombre);
        this.puntosKi = validar(puntosKi);
    }

    /**
     * Regla de D&amp;D 5e: un monje tiene como máximo tantos puntos de Ki como su nivel.
     */
    private int validar(int valor) {
        Validaciones.rango(valor, MINIMO, MAXIMO, "Puntos de Ki", Campo.ATRIBUTO);
        if (valor > getNivel()) {
            throw new DatosInvalidosException(Campo.ATRIBUTO, "Puntos de Ki: un Monje de nivel "
                    + getNivel() + " tiene como máximo " + getNivel() + " puntos de Ki.");
        }
        return valor;
    }

    public int getPuntosKi() {
        return puntosKi;
    }

    @Override
    public String getClase() {
        return "Monje";
    }

    @Override
    public String getAtributoEspecial() {
        return "Puntos de Ki: " + puntosKi;
    }

    @Override
    public String getValorAtributo() {
        return String.valueOf(puntosKi);
    }

    @Override
    public String simularCombate() {
        return "El Monje " + getNombre()
                + " gasta puntos de Ki para ejecutar una Ráfaga de Golpes y un Ataque Aturdidor.";
    }

    @Override
    public int getBonificadorIniciativa() {
        return 3;
    }

    /** Dado de Artes Marciales de 5e: d4 (1-4), d6 (5-10), d8 (11-16), d10 (17-20). */
    public static int dadoArtesMarciales(int nivel) {
        return nivel <= 4 ? 4 : nivel <= 10 ? 6 : nivel <= 16 ? 8 : 10;
    }

    /**
     * Ráfaga de Golpes: dos golpes desarmados; si alguno impacta intenta un
     * Ataque Aturdidor (salvación del enemigo contra la CD del monje).
     */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        int caras = dadoArtesMarciales(getNivel());
        Golpe primero = golpear(dados, enemigo, caras);
        Golpe segundo = golpear(dados, enemigo, caras);
        StringBuilder detalle = new StringBuilder(primero.describir("Golpe 1", "contundente"))
                .append(" · ").append(segundo.describir("Golpe 2", "contundente"));
        if (primero.impacta() || segundo.impacta()) {
            boolean resiste = enemigo.superaSalvacion(dados, getCdSalvacion());
            detalle.append(resiste ? " · resiste el aturdimiento" : " · ¡enemigo aturdido!");
        }
        return new ResultadoTurno(detalle.toString(), primero.danio() + segundo.danio(), 0);
    }
}
