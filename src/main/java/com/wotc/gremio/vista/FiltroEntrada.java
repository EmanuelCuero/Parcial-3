package com.wotc.gremio.vista;

import java.awt.Toolkit;
import java.util.function.IntPredicate;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Filtro que valida mientras el usuario escribe: rechaza caracteres no permitidos
 * (por ejemplo letras en un campo numérico) y limita la longitud.
 */
public class FiltroEntrada extends DocumentFilter {

    private final IntPredicate permitido;
    private final int longitudMaxima;

    public FiltroEntrada(IntPredicate permitido, int longitudMaxima) {
        this.permitido = permitido;
        this.longitudMaxima = longitudMaxima;
    }

    /** Solo dígitos, hasta {@code maximo} caracteres. */
    public static FiltroEntrada digitos(int maximo) {
        return new FiltroEntrada(Character::isDigit, maximo);
    }

    /** Letras (con tildes), espacios, apóstrofo, guion y punto. */
    public static FiltroEntrada letras(int maximo) {
        return new FiltroEntrada(c -> Character.isLetter(c) || c == ' ' || c == '\''
                || c == '-' || c == '.', maximo);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, texto, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet attrs)
            throws BadLocationException {
        String nuevo = texto == null ? "" : texto;
        int longitudFinal = fb.getDocument().getLength() - longitud + nuevo.length();
        if (nuevo.chars().allMatch(permitido) && longitudFinal <= longitudMaxima) {
            super.replace(fb, offset, longitud, nuevo, attrs);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
