package com.wotc.gremio.excepciones;

/**
 * Ya existe en el gremio un aventurero con el mismo nombre.
 */
public class AventureroDuplicadoException extends GremioException {

    private final String nombre;

    public AventureroDuplicadoException(String nombre) {
        super("Ya existe un aventurero llamado \"" + nombre + "\" en el gremio.");
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
