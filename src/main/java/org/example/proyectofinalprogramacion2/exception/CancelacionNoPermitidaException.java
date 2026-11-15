package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando una compra no puede cancelarse.
 */
public class CancelacionNoPermitidaException extends CinemaUQException {

    public CancelacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
