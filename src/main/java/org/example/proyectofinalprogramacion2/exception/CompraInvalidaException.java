package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando una compra no cumple sus reglas de armado (sin entradas, entradas de otra función o redención con más de un beneficio).
 */
public class CompraInvalidaException extends CinemaUQException {

    public CompraInvalidaException(String mensaje) {
        super(mensaje);
    }
}
