package com.wotc.gremio.excepciones;

import java.io.IOException;

/**
 * No se pudo leer o escribir el archivo donde se guardan los aventureros.
 */
public class ArchivoGremioException extends GremioException {

    public ArchivoGremioException(String accion, IOException causa) {
        super("No se pudo " + accion + " el archivo del gremio: " + causa.getMessage(), causa);
    }
}
