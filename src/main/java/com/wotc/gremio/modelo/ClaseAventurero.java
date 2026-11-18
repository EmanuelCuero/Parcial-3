package com.wotc.gremio.modelo;

import java.util.Arrays;
import java.util.Optional;

/**
 * Clases de personaje disponibles, el atributo especial que solicita cada una
 * y los textos de ayuda que muestra la interfaz.
 */
public enum ClaseAventurero {

    GUERRERO("Guerrero", "¿Usa armadura pesada?", TipoAtributo.BOOLEANO,
            "Maestro de las armas y la armadura. Resiste en primera línea.",
            "Marca la casilla si combate con armadura pesada."),
    MAGO("Mago", "Espacios de conjuro:", TipoAtributo.ENTERO,
            "Erudito arcano que doblega la realidad con conjuros.",
            "Número entero entre " + Mago.MINIMO + " y " + Mago.MAXIMO + "."),
    CLERIGO("Clérigo", "Dios venerado:", TipoAtributo.TEXTO,
            "Intermediario divino: cura aliados y castiga a los no muertos.",
            "Nombre de la deidad (2 a 40 letras)."),
    MONJE("Monje", "Puntos de Ki:", TipoAtributo.ENTERO,
            "Artista marcial que canaliza el Ki en golpes veloces.",
            "Número entero entre 0 y el nivel del monje."),
    PALADIN("Paladín", "Puntos de fuerza:", TipoAtributo.ENTERO,
            "Guerrero sagrado ligado a un juramento de justicia.",
            "Puntuación de Fuerza entre " + Paladin.MINIMO + " y " + Paladin.MAXIMO + "."),
    EXPLORADOR("Explorador", "Terreno favorecido:", TipoAtributo.TEXTO,
            "Cazador experto en rastrear a su presa por tierras salvajes.",
            "Por ejemplo: Bosque, Montaña, Desierto (2 a 40 letras)."),
    PICARO("Pícaro", "¿Tiene herramientas de ladrón?", TipoAtributo.BOOLEANO,
            "Especialista en sigilo, cerraduras y golpes precisos.",
            "Marca la casilla si lleva herramientas de ladrón."),
    BARDO("Bardo", "Instrumento musical:", TipoAtributo.TEXTO,
            "Artista cuya música inspira a sus aliados y teje magia.",
            "Por ejemplo: Laúd, Flauta, Tambor (2 a 40 letras).");

    /** Tipo de dato del atributo especial. */
    public enum TipoAtributo {
        BOOLEANO, ENTERO, TEXTO
    }

    private final String nombreVisible;
    private final String etiquetaAtributo;
    private final TipoAtributo tipoAtributo;
    private final String descripcion;
    private final String ayudaAtributo;

    ClaseAventurero(String nombreVisible, String etiquetaAtributo, TipoAtributo tipoAtributo,
            String descripcion, String ayudaAtributo) {
        this.nombreVisible = nombreVisible;
        this.etiquetaAtributo = etiquetaAtributo;
        this.tipoAtributo = tipoAtributo;
        this.descripcion = descripcion;
        this.ayudaAtributo = ayudaAtributo;
    }

    /**
     * Busca la clase por su nombre visible ("Clérigo", "Pícaro"...).
     */
    public static Optional<ClaseAventurero> desdeNombre(String nombre) {
        return Arrays.stream(values())
                .filter(clase -> clase.nombreVisible.equalsIgnoreCase(nombre))
                .findFirst();
    }

    public String getEtiquetaAtributo() {
        return etiquetaAtributo;
    }

    public TipoAtributo getTipoAtributo() {
        return tipoAtributo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getAyudaAtributo() {
        return ayudaAtributo;
    }

    @Override
    public String toString() {
        return nombreVisible;
    }
}
