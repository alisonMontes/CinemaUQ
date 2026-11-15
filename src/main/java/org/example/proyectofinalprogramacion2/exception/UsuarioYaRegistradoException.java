package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando ya existe un usuario con el mismo correo.
 */
public class UsuarioYaRegistradoException extends CinemaUQException {

    public UsuarioYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}
