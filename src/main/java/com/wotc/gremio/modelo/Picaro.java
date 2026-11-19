package com.wotc.gremio.modelo;

public class Picaro extends Aventurero {

    private final boolean herramientasLadron;

    public Picaro(String nombre, int nivel, int puntosVida, boolean herramientasLadron) {
        super(nombre, nivel, puntosVida);
        this.herramientasLadron = herramientasLadron;
    }

    /** Sobrecarga: Pícaro novato (nivel 1, vida por defecto). */
    public Picaro(String nombre, boolean herramientasLadron) {
        super(nombre);
        this.herramientasLadron = herramientasLadron;
    }

    public boolean isHerramientasLadron() {
        return herramientasLadron;
    }

    @Override
    public String getClase() {
        return "Pícaro";
    }

    @Override
    public String getAtributoEspecial() {
        return "Herramientas de ladrón: " + (herramientasLadron ? "Sí" : "No");
    }

    @Override
    public String getValorAtributo() {
        return String.valueOf(herramientasLadron);
    }

    @Override
    public String simularCombate() {
        return "El Pícaro " + getNombre()
                + " aprovecha la ventaja táctica para asestar un Ataque Furtivo masivo y se reposiciona.";
    }

    @Override
    public int getBonificadorIniciativa() {
        return 4;
    }

    /** Dados de Ataque Furtivo de 5e: la mitad del nivel redondeada hacia arriba. */
    public int getDadosAtaqueFurtivo() {
        return (getNivel() + 1) / 2;
    }

    /** Espada corta (1d6 + 3) y, si impacta, Ataque Furtivo de Xd6. */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        Golpe golpe = golpear(dados, enemigo, 6);
        if (!golpe.impacta()) {
            return new ResultadoTurno(golpe.describir("Espada corta", "perforante"), 0, 0);
        }
        int furtivo = dados.tirarDanio(getDadosAtaqueFurtivo(), 6, golpe.tirada().esCritico());
        return new ResultadoTurno(golpe.describir("Espada corta", "perforante") + " + Ataque Furtivo "
                + getDadosAtaqueFurtivo() + "d6 = " + furtivo, golpe.danio() + furtivo, 0);
    }
}
