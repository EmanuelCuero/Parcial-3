package com.wotc.gremio.modelo;

import java.util.Random;

/**
 * Generador de tiradas de dados (d4, d6, d8, d20...).
 * Se puede crear con semilla para obtener combates reproducibles.
 */
public class Dados {

    private final Random aleatorio;

    /** Dados realmente aleatorios. */
    public Dados() {
        this(new Random());
    }

    /** Sobrecarga: dados con semilla fija (mismo resultado en cada ejecución). */
    public Dados(long semilla) {
        this(new Random(semilla));
    }

    private Dados(Random aleatorio) {
        this.aleatorio = aleatorio;
    }

    /** Tira un dado de {@code caras} caras: resultado entre 1 y caras. */
    public int tirar(int caras) {
        if (caras < 2) {
            throw new IllegalArgumentException("Un dado necesita al menos 2 caras.");
        }
        return aleatorio.nextInt(caras) + 1;
    }

    /** Sobrecarga: tira {@code cantidad} dados y suma (por ejemplo 8d6). */
    public int tirar(int cantidad, int caras) {
        int suma = 0;
        for (int i = 0; i < cantidad; i++) {
            suma += tirar(caras);
        }
        return suma;
    }

    /** Tira el daño; en un crítico se duplica la cantidad de dados (regla 5e). */
    public int tirarDanio(int cantidad, int caras, boolean critico) {
        return tirar(critico ? cantidad * 2 : cantidad, caras);
    }

    /** Tirada de ataque: d20 + bonificador contra la clase de armadura. */
    public TiradaAtaque atacar(int bonificador, int claseArmadura) {
        return new TiradaAtaque(tirar(20), bonificador, claseArmadura);
    }
}
