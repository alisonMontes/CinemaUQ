package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.exception.AccesoDenegadoException;
import org.example.proyectofinalprogramacion2.model.Administrador;
import org.example.proyectofinalprogramacion2.model.Usuario;

/**
 * Comprobación de permisos compartida por los servicios de administración.
 */
// SRP: solo verifica que quien opera sea administrador.
final class ValidadorAcceso {

    private ValidadorAcceso() {
    }

    static void exigirAdministrador(Usuario usuario) throws AccesoDenegadoException {
        if (!(usuario instanceof Administrador)) {
            throw new AccesoDenegadoException("Solo un administrador puede realizar esta operación");
        }
    }
}
