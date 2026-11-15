package org.example.proyectofinalprogramacion2.model;

import java.util.Objects;

/**
 * Usuario del sistema. Se identifica por su correo.
 */
// LSP: Cliente y Administrador la sustituyen; el login solo usa verificarClave() y getRol().
public abstract class Usuario {

    private String nombre;
    private String correo;
    private String clave;

    protected Usuario(String nombre, String correo, String clave) {
        setNombre(nombre);
        setCorreo(correo);
        setClave(clave);
    }

    public boolean verificarClave(String clave) {
        return this.clave.equals(clave);
    }

    /**
     * Rol del usuario dentro del sistema (CLIENTE o ADMINISTRADOR).
     */
    public abstract String getRol();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validador.texto(nombre, "nombre");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        String valor = Validador.texto(correo, "correo").toLowerCase();
        if (!valor.contains("@")) {
            throw new IllegalArgumentException("El correo no es válido");
        }
        this.correo = valor;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = Validador.texto(clave, "clave");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return correo.equals(usuario.correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correo);
    }

    @Override
    public String toString() {
        return getRol() + "{nombre='" + nombre + "', correo='" + correo + "'}";
    }
}
