package com.wotc.gremio.excepciones;

/**
 * Excepción base (comprobada) de las reglas de negocio del gremio.
 */
public class GremioException extends Exception {

    public GremioException(String mensaje) {
        super(mensaje);
    }

    public GremioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
