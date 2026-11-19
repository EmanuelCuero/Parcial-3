package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

public class Paladin extends Aventurero {

    public static final int MINIMO = 1;
    public static final int MAXIMO = 30;

    private final int puntosFuerza;

    public Paladin(String nombre, int nivel, int puntosVida, int puntosFuerza) {
        super(nombre, nivel, puntosVida);
        this.puntosFuerza = validar(puntosFuerza);
    }

    /** Sobrecarga: Paladín novato (nivel 1, vida por defecto). */
    public Paladin(String nombre, int puntosFuerza) {
        super(nombre);
        this.puntosFuerza = validar(puntosFuerza);
    }

    private static int validar(int valor) {
        return Validaciones.rango(valor, MINIMO, MAXIMO, "Puntos de fuerza", Campo.ATRIBUTO);
    }

    public int getPuntosFuerza() {
        return puntosFuerza;
    }

    @Override
    public String getClase() {
        return "Paladín";
    }

    @Override
    public String getAtributoEspecial() {
        return "Puntos de fuerza: " + puntosFuerza;
    }

    @Override
    public String getValorAtributo() {
        return String.valueOf(puntosFuerza);
    }

    @Override
    public String simularCombate() {
        return "El Paladín " + getNombre()
                + " consume magia para imbuir su arma con un Castigo Divino de daño radiante.";
    }

    /** Modificador de característica de 5e: (puntuación - 10) / 2, redondeado hacia abajo. */
    public int getModificadorFuerza() {
        return Math.floorDiv(puntosFuerza - 10, 2);
    }

    /** El paladín ataca con su Fuerza en lugar del modificador estándar. */
    @Override
    public int getBonificadorAtaque() {
        return getBonoCompetencia() + getModificadorFuerza();
    }

    /** Espada larga (1d8 + Fuerza) y, si impacta, Castigo Divino de +2d8 radiante. */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        Golpe golpe = golpear(dados, enemigo, getBonificadorAtaque(), 1, 8, getModificadorFuerza());
        if (!golpe.impacta()) {
            return new ResultadoTurno(golpe.describir("Espada", "cortante"), 0, 0);
        }
        int castigo = dados.tirarDanio(2, 8, golpe.tirada().esCritico());
        return new ResultadoTurno(golpe.describir("Espada", "cortante") + " + Castigo Divino "
                + castigo + " radiante", golpe.danio() + castigo, 0);
    }
}
