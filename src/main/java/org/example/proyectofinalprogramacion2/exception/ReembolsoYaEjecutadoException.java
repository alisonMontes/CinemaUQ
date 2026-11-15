package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando se intenta reembolsar una compra ya reembolsada.
 */
public class ReembolsoYaEjecutadoException extends CinemaUQException {

    public ReembolsoYaEjecutadoException(String mensaje) {
        super(mensaje);
    }
}
