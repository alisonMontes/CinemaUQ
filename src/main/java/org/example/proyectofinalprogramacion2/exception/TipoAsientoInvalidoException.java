package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando se intenta crear una entrada para un asiento de otro tipo
 * (entrada general en asiento accesible o al revés).
 */
public class TipoAsientoInvalidoException extends CinemaUQException {

    public TipoAsientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
