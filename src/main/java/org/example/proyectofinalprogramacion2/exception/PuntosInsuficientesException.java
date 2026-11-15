package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando los puntos vigentes no alcanzan para una redención.
 */
public class PuntosInsuficientesException extends CinemaUQException {

    public PuntosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
