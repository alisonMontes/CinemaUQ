package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando el correo o la clave no son válidos al iniciar sesión.
 */
public class CredencialesInvalidasException extends CinemaUQException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
