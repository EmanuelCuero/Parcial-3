package com.wotc.gremio.modelo;

/**
 * Criatura a la que se enfrenta el gremio (valores del SRD de D&amp;D 5e).
 *
 * @param nombre        nombre de la criatura
 * @param claseArmadura CA que deben superar los ataques
 * @param puntosVida    puntos de vida iniciales
 * @param bonoSalvacion bonificador a sus tiradas de salvación
 */
public record Enemigo(String nombre, int claseArmadura, int puntosVida, int bonoSalvacion) {

    public static final Enemigo OGRO = new Enemigo("Ogro", 11, 59, 0);
    public static final Enemigo TROLL = new Enemigo("Troll", 15, 84, 1);
    public static final Enemigo DRAGON_JOVEN = new Enemigo("Dragón Rojo Joven", 18, 178, 4);
    public static final Enemigo DRAGON_ADULTO = new Enemigo("Dragón Rojo Adulto", 19, 256, 6);

    /** Elige un desafío acorde al nivel promedio del gremio. */
    public static Enemigo segunNivel(double nivelPromedio) {
        if (nivelPromedio < 4) {
            return OGRO;
        }
        if (nivelPromedio < 8) {
            return TROLL;
        }
        if (nivelPromedio < 13) {
            return DRAGON_JOVEN;
        }
        return DRAGON_ADULTO;
    }

    /** Tirada de salvación del enemigo contra una dificultad (CD). */
    public boolean superaSalvacion(Dados dados, int dificultad) {
        return dados.tirar(20) + bonoSalvacion >= dificultad;
    }
}
