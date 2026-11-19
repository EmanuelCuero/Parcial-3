package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

/**
 * Clase base abstracta de todos los aventureros del gremio.
 * Las clases hijas definen su atributo especial y su táctica de combate.
 */
public abstract class Aventurero implements IAccionCombate {

    public static final int NIVEL_MINIMO = 1;
    public static final int NIVEL_MAXIMO = 20;
    public static final int PUNTOS_VIDA_MINIMOS = 1;
    public static final int PUNTOS_VIDA_MAXIMOS = 999;
    public static final int NIVEL_POR_DEFECTO = 1;
    public static final int PUNTOS_VIDA_POR_DEFECTO = 10;

    private final String nombre;
    private final int nivel;
    private final int puntosVida;

    /**
     * Constructor completo.
     *
     * @param nombre     nombre del aventurero (2 a 30 letras)
     * @param nivel      nivel entre 1 y 20
     * @param puntosVida puntos de vida entre 1 y 999
     */
    protected Aventurero(String nombre, int nivel, int puntosVida) {
        this.nombre = Validaciones.texto(nombre, "Nombre",
                Validaciones.LONGITUD_MAXIMA_NOMBRE, Campo.NOMBRE);
        this.nivel = Validaciones.rango(nivel, NIVEL_MINIMO, NIVEL_MAXIMO,
                "Nivel", Campo.NIVEL);
        this.puntosVida = Validaciones.rango(puntosVida, PUNTOS_VIDA_MINIMOS,
                PUNTOS_VIDA_MAXIMOS, "Puntos de vida", Campo.PUNTOS_VIDA);
    }

    /**
     * Constructor sobrecargado: crea un aventurero novato de nivel 1
     * con los puntos de vida por defecto.
     *
     * @param nombre nombre del aventurero
     */
    protected Aventurero(String nombre) {
        this(nombre, NIVEL_POR_DEFECTO, PUNTOS_VIDA_POR_DEFECTO);
    }

    public String getNombre() {
        return nombre;
    }

    public int getNivel() {
        return nivel;
    }

    public int getPuntosVida() {
        return puntosVida;
    }

    /**
     * @return el nombre de la clase de personaje (Guerrero, Mago, ...)
     */
    public abstract String getClase();

    /**
     * @return el atributo especial de la clase, listo para mostrar
     */
    public abstract String getAtributoEspecial();

    /**
     * @return el valor del atributo especial sin etiqueta (por ejemplo "true", "4"
     *         o "Lathander"), usado para guardar el gremio en un archivo
     */
    public abstract String getValorAtributo();

    /**
     * Indica si el aventurero apoya al grupo en lugar de atacar o curar.
     * Las clases de apoyo (como el Bardo) lo sobrescriben.
     */
    public boolean esApoyo() {
        return false;
    }

    // ------------------------------------------------------------------
    // Mecánicas de combate con dados (D&D 5e)
    // ------------------------------------------------------------------

    /** Modificador de la característica principal de un héroe estándar (+3). */
    public static final int MODIFICADOR_BASE = 3;

    /**
     * Resuelve con dados la táctica de la clase contra el enemigo.
     * Cada subclase la sobrescribe con su mecánica propia (polimorfismo).
     */
    public abstract ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo);

    /** Bonificador de competencia de 5e: +2 en nivel 1-4, +3 en 5-8 ... +6 en 17-20. */
    public int getBonoCompetencia() {
        return 2 + (nivel - 1) / 4;
    }

    /** Bonificador de ataque: competencia + modificador de característica. */
    public int getBonificadorAtaque() {
        return getBonoCompetencia() + MODIFICADOR_BASE;
    }

    /** Dificultad (CD) de las salvaciones que provoca: 8 + competencia + modificador. */
    public int getCdSalvacion() {
        return 8 + getBonoCompetencia() + MODIFICADOR_BASE;
    }

    /** Bonificador a la iniciativa (por defecto 0; las clases ágiles lo aumentan). */
    public int getBonificadorIniciativa() {
        return 0;
    }

    /**
     * Ataque con arma: d20 + bonificador contra la CA; si impacta tira el daño
     * (duplicando dados en crítico) y suma el bonificador de daño.
     */
    protected Golpe golpear(Dados dados, Enemigo enemigo, int bonificadorAtaque,
            int cantidadDados, int caras, int bonificadorDanio) {
        TiradaAtaque tirada = dados.atacar(bonificadorAtaque, enemigo.claseArmadura());
        int danio = tirada.impacta()
                ? Math.max(1, dados.tirarDanio(cantidadDados, caras, tirada.esCritico()) + bonificadorDanio)
                : 0;
        return new Golpe(tirada, danio);
    }

    /** Sobrecarga: ataque con el bonificador de ataque de la clase y +3 al daño. */
    protected Golpe golpear(Dados dados, Enemigo enemigo, int caras) {
        return golpear(dados, enemigo, getBonificadorAtaque(), 1, caras, MODIFICADOR_BASE);
    }

    /**
     * @return descripción breve: clase y nombre
     */
    public String describir() {
        return getClase() + " " + nombre;
    }

    /**
     * Sobrecarga de {@link #describir()}.
     *
     * @param detallada si es verdadero incluye nivel, vida y atributo especial
     * @return descripción breve o detallada
     */
    public String describir(boolean detallada) {
        if (!detallada) {
            return describir();
        }
        return describir() + " | Nivel " + nivel + " | PV " + puntosVida
                + " | " + getAtributoEspecial();
    }

    @Override
    public String toString() {
        return describir(true);
    }
}
