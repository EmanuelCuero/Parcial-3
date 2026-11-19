package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;

public class Explorador extends Aventurero {

    private final String terrenoFavorecido;

    public Explorador(String nombre, int nivel, int puntosVida, String terrenoFavorecido) {
        super(nombre, nivel, puntosVida);
        this.terrenoFavorecido = validar(terrenoFavorecido);
    }

    /** Sobrecarga: Explorador novato (nivel 1, vida por defecto). */
    public Explorador(String nombre, String terrenoFavorecido) {
        super(nombre);
        this.terrenoFavorecido = validar(terrenoFavorecido);
    }

    private static String validar(String valor) {
        return Validaciones.texto(valor, "Terreno favorecido", Validaciones.LONGITUD_MAXIMA_TEXTO, Campo.ATRIBUTO);
    }

    public String getTerrenoFavorecido() {
        return terrenoFavorecido;
    }

    @Override
    public String getClase() {
        return "Explorador";
    }

    @Override
    public String getAtributoEspecial() {
        return "Terreno favorecido: " + terrenoFavorecido;
    }

    @Override
    public String getValorAtributo() {
        return terrenoFavorecido;
    }

    @Override
    public String simularCombate() {
        return "El Explorador " + getNombre()
                + " aplica Marca del Cazador sobre el objetivo para maximizar su daño sostenido.";
    }

    @Override
    public int getBonificadorIniciativa() {
        return 3;
    }

    /** Arco largo (1d8 + 3) y, si impacta, +1d6 por la Marca del Cazador. */
    @Override
    public ResultadoTurno resolverTurno(Dados dados, Enemigo enemigo) {
        Golpe flecha = golpear(dados, enemigo, 8);
        if (!flecha.impacta()) {
            return new ResultadoTurno(flecha.describir("Flecha", "perforante"), 0, 0);
        }
        int marca = dados.tirarDanio(1, 6, flecha.tirada().esCritico());
        return new ResultadoTurno(flecha.describir("Flecha", "perforante") + " + Marca del Cazador "
                + marca, flecha.danio() + marca, 0);
    }
}
