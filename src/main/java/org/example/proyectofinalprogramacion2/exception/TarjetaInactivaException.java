package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando la tarjeta virtual está bloqueada.
 */
public class TarjetaInactivaException extends CinemaUQException {

    public TarjetaInactivaException(String mensaje) {
        super(mensaje);
    }
}
