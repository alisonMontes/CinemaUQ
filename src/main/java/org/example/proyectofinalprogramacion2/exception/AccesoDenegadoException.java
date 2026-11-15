package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando un usuario intenta una operación que no le corresponde (por ejemplo, un no administrador).
 */
public class AccesoDenegadoException extends CinemaUQException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
