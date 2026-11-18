package com.wotc.gremio.excepciones;

import java.io.IOException;

/**
 * No se pudo escribir en el archivo de historial del gremio.
 */
public class HistorialException extends GremioException {

    public HistorialException(String accion, IOException causa) {
        super("No se pudo " + accion + " en el historial: " + causa.getMessage(), causa);
    }
}
