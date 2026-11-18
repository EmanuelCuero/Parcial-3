package com.wotc.gremio.modelo;

import com.wotc.gremio.excepciones.DatosInvalidosException;
import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;
import java.util.regex.Pattern;

/**
 * Reglas de validación compartidas por todo el modelo.
 */
public final class Validaciones {

    public static final int LONGITUD_MINIMA_TEXTO = 2;
    public static final int LONGITUD_MAXIMA_NOMBRE = 30;
    public static final int LONGITUD_MAXIMA_TEXTO = 40;

    /** Letras (con tildes y ñ), espacios, apóstrofo, guion y punto; debe empezar con letra. */
    private static final Pattern TEXTO_VALIDO = Pattern.compile("\\p{L}[\\p{L} '\\-.]*");

    private Validaciones() {
    }

    /**
     * Valida un texto obligatorio formado solo por letras.
     *
     * @return el texto sin espacios sobrantes
     */
    public static String texto(String valor, String descripcion, int maximo, Campo campo) {
        String limpio = valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
        if (limpio.isEmpty()) {
            throw new DatosInvalidosException(campo, descripcion + ": es obligatorio.");
        }
        if (limpio.length() < LONGITUD_MINIMA_TEXTO || limpio.length() > maximo) {
            throw new DatosInvalidosException(campo, descripcion + ": debe tener entre "
                    + LONGITUD_MINIMA_TEXTO + " y " + maximo + " caracteres.");
        }
        if (!TEXTO_VALIDO.matcher(limpio).matches()) {
            throw new DatosInvalidosException(campo, descripcion
                    + ": solo admite letras, espacios, apóstrofos y guiones.");
        }
        return limpio;
    }

    /**
     * Valida que un número esté dentro de un rango cerrado.
     */
    public static int rango(int valor, int minimo, int maximo, String descripcion, Campo campo) {
        if (valor < minimo || valor > maximo) {
            throw new DatosInvalidosException(campo, descripcion + ": debe estar entre "
                    + minimo + " y " + maximo + ".");
        }
        return valor;
    }
}
