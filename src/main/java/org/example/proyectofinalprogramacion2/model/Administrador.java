package org.example.proyectofinalprogramacion2.model;

/**
 * Usuario que gestiona el cine.
 */
// LSP: puede usarse donde se espere un Usuario (inicio de sesión, listados) sin romper su contrato.
public class Administrador extends Usuario {

    public Administrador(String nombre, String correo, String clave) {
        super(nombre, correo, clave);
    }

    @Override
    public String getRol() {
        return "ADMINISTRADOR";
    }
}
