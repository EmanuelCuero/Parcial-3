package com.wotc.gremio.excepciones;

/**
 * Un dato del aventurero no cumple las reglas del gremio.
 * Indica qué campo falló para que la vista pueda resaltarlo.
 */
public class DatosInvalidosException extends IllegalArgumentException {

    /** Campos del formulario de registro que pueden fallar. */
    public enum Campo {
        NOMBRE, NIVEL, PUNTOS_VIDA, ATRIBUTO
    }

    private final Campo campo;

    public DatosInvalidosException(Campo campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public DatosInvalidosException(Campo campo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.campo = campo;
    }

    public Campo getCampo() {
        return campo;
    }
}
