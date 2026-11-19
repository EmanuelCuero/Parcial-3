package com.wotc.gremio.excepciones;

/**
 * Una línea del archivo del gremio no tiene el formato esperado o contiene
 * datos que no cumplen las reglas (por ejemplo, editada a mano).
 */
public class FormatoArchivoException extends GremioException {

    private final int numeroLinea;

    public FormatoArchivoException(int numeroLinea, String motivo) {
        super("Línea " + numeroLinea + " del archivo del gremio: " + motivo);
        this.numeroLinea = numeroLinea;
    }

    public FormatoArchivoException(int numeroLinea, String motivo, Throwable causa) {
        super("Línea " + numeroLinea + " del archivo del gremio: " + motivo, causa);
        this.numeroLinea = numeroLinea;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }
}
