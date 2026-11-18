package com.wotc.gremio.modelo;

public class Guerrero extends Aventurero {

    private final boolean armaduraPesada;

    public Guerrero(String nombre, int nivel, int puntosVida, boolean armaduraPesada) {
        super(nombre, nivel, puntosVida);
        this.armaduraPesada = armaduraPesada;
    }

    /** Sobrecarga: Guerrero novato (nivel 1, vida por defecto). */
    public Guerrero(String nombre, boolean armaduraPesada) {
        super(nombre);
        this.armaduraPesada = armaduraPesada;
    }

    public boolean isArmaduraPesada() {
        return armaduraPesada;
    }

    @Override
    public String getClase() {
        return "Guerrero";
    }

    @Override
    public String getAtributoEspecial() {
        return "Armadura pesada: " + (armaduraPesada ? "Sí" : "No");
    }

    @Override
    public String simularCombate() {
        return "El Guerrero " + getNombre()
                + " usa Oleada de Acción para realizar múltiples ataques físicos continuos.";
    }

    @Override
    public int getBonificadorIniciativa() {
        return 1;
    }

    /** Oleada de Acción: dos ataques con espada larga (1d8 + 3 cortante). */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        Golpe primero = golpear(dados, enemigo, 8);
        Golpe segundo = golpear(dados, enemigo, 8);
        return new ResultadoTurno(primero.describir("Ataque 1", "cortante") + " · "
                + segundo.describir("Ataque 2", "cortante"), primero.danio() + segundo.danio(), 0);
    }
}
