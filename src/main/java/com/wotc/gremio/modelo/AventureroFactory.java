package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException;
import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

/**
 * Crea el aventurero concreto según la clase elegida.
 * Los métodos {@code crear} están sobrecargados según el tipo del atributo especial.
 */
public final class AventureroFactory {

    private AventureroFactory() {
    }

    /**
     * Crea un aventurero cuyo atributo especial es sí/no (Guerrero, Pícaro).
     */
    public static Aventurero crear(ClaseAventurero clase, String nombre, int nivel,
            int puntosVida, boolean atributo) {
        switch (clase) {
            case GUERRERO:
                return new Guerrero(nombre, nivel, puntosVida, atributo);
            case PICARO:
                return new Picaro(nombre, nivel, puntosVida, atributo);
            default:
                throw new IllegalArgumentException(
                        "La clase " + clase + " no usa un atributo de sí/no.");
        }
    }

    /**
     * Crea un aventurero cuyo atributo especial es un número entero
     * (Mago, Monje, Paladín).
     */
    public static Aventurero crear(ClaseAventurero clase, String nombre, int nivel,
            int puntosVida, int atributo) {
        switch (clase) {
            case MAGO:
                return new Mago(nombre, nivel, puntosVida, atributo);
            case MONJE:
                return new Monje(nombre, nivel, puntosVida, atributo);
            case PALADIN:
                return new Paladin(nombre, nivel, puntosVida, atributo);
            default:
                throw new IllegalArgumentException(
                        "La clase " + clase + " no usa un atributo numérico.");
        }
    }

    /**
     * Crea un aventurero a partir del atributo escrito como texto.
     * Para las clases numéricas convierte el texto a entero.
     */
    public static Aventurero crear(ClaseAventurero clase, String nombre, int nivel,
            int puntosVida, String atributo) {
        switch (clase) {
            case CLERIGO:
                return new Clerigo(nombre, nivel, puntosVida, atributo);
            case EXPLORADOR:
                return new Explorador(nombre, nivel, puntosVida, atributo);
            case BARDO:
                return new Bardo(nombre, nivel, puntosVida, atributo);
            case MAGO:
            case MONJE:
            case PALADIN:
                return crear(clase, nombre, nivel, puntosVida, convertirEntero(clase, atributo));
            default:
                throw new IllegalArgumentException(
                        "La clase " + clase + " no usa un atributo de texto.");
        }
    }

    private static int convertirEntero(ClaseAventurero clase, String texto) {
        String campo = clase.getEtiquetaAtributo().replace(":", "");
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            throw new DatosInvalidosException(Campo.ATRIBUTO, campo + ": es obligatorio.");
        }
        try {
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(Campo.ATRIBUTO,
                    campo + ": debe ser un número entero.", e);
        }
    }
}
