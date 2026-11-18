package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

public class Mago extends Aventurero {

    public static final int MINIMO = 0;
    public static final int MAXIMO = 30;

    private final int espaciosConjuro;

    public Mago(String nombre, int nivel, int puntosVida, int espaciosConjuro) {
        super(nombre, nivel, puntosVida);
        this.espaciosConjuro = validar(espaciosConjuro);
    }

    /** Sobrecarga: Mago novato (nivel 1, vida por defecto). */
    public Mago(String nombre, int espaciosConjuro) {
        super(nombre);
        this.espaciosConjuro = validar(espaciosConjuro);
    }

    private static int validar(int valor) {
        return Validaciones.rango(valor, MINIMO, MAXIMO, "Espacios de conjuro", Campo.ATRIBUTO);
    }

    public int getEspaciosConjuro() {
        return espaciosConjuro;
    }

    @Override
    public String getClase() {
        return "Mago";
    }

    @Override
    public String getAtributoEspecial() {
        return "Espacios de conjuro: " + espaciosConjuro;
    }

    @Override
    public String simularCombate() {
        return "El Mago " + getNombre()
                + " gasta un espacio de conjuro para lanzar una poderosa Bola de Fuego.";
    }

    @Override
    public int getBonificadorIniciativa() {
        return 1;
    }

    /**
     * Bola de Fuego: 8d6 de fuego sin tirada de ataque; el enemigo tira salvación
     * contra la CD del mago y, si la supera, recibe la mitad.
     */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        int fuego = dados.tirar(8, 6);
        boolean salva = enemigo.superaSalvacion(dados, getCdSalvacion());
        int danio = salva ? fuego / 2 : fuego;
        return new ResultadoTurno("Bola de Fuego 8d6 = " + fuego + " · salvación contra CD "
                + getCdSalvacion() + (salva ? ": la supera, mitad de daño" : ": falla")
                + " → " + danio + " fuego", danio, 0);
    }
}
