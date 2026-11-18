package com.wotc.gremio.vista;

import java.awt.Color;
import java.net.URL;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Insignias (iconos) y colores de cada clase de aventurero.
 * Las imágenes están en {@code /com/wotc/gremio/iconos/<clase>_<tamaño>.png}.
 */
public final class IconosClase {

    private static final String CARPETA = "/com/wotc/gremio/iconos/";
    private static final Map<String, Icon> CACHE = new HashMap<>();
    private static final Map<String, Color> COLORES = Map.of(
            "guerrero", new Color(192, 57, 43),
            "mago", new Color(108, 74, 182),
            "clerigo", new Color(176, 124, 14),
            "monje", new Color(30, 140, 126),
            "paladin", new Color(47, 109, 181),
            "explorador", new Color(78, 138, 62),
            "picaro", new Color(61, 68, 81),
            "bardo", new Color(192, 69, 122));
    private static final Color COLOR_POR_DEFECTO = new Color(42, 51, 88);

    private IconosClase() {
    }

    /**
     * @param clase nombre visible de la clase ("Clérigo", "Pícaro"...)
     * @param tamano 24, 32 o 64 píxeles
     * @return la insignia de la clase, o {@code null} si no existe
     */
    public static Icon de(String clase, int tamano) {
        if (clase == null) {
            return null;
        }
        return CACHE.computeIfAbsent(clave(clase) + "_" + tamano, nombre -> {
            URL recurso = IconosClase.class.getResource(CARPETA + nombre + ".png");
            return recurso == null ? null : new ImageIcon(recurso);
        });
    }

    /** Color característico de la clase. */
    public static Color color(String clase) {
        return clase == null ? COLOR_POR_DEFECTO
                : COLORES.getOrDefault(clave(clase), COLOR_POR_DEFECTO);
    }

    /** "Clérigo" → "clerigo": sin tildes y en minúsculas. */
    static String clave(String clase) {
        return Normalizer.normalize(clase, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
