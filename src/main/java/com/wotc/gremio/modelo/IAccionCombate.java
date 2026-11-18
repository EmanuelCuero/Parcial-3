package com.wotc.gremio.modelo;

/**
 * Contrato de combate: toda clase de aventurero debe implementar su táctica.
 */
public interface IAccionCombate {

    /**
     * Ejecuta la táctica de combate propia de la clase.
     *
     * @return el mensaje que describe la acción realizada en combate
     */
    String simularCombate();
}
