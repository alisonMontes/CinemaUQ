package org.example.proyectofinalprogramacion2.model;

import java.util.Objects;

/**
 * Producto de confitería que se vende por unidad.
 */
public class Producto implements ItemVendible {

    private String codigo;
    private String nombre;
    private double precio;
    private String descripcion;

    public Producto(String codigo, String nombre, double precio, String descripcion) {
        setCodigo(codigo);
        setNombre(nombre);
        setPrecio(precio);
        setDescripcion(descripcion);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = Validador.texto(codigo, "código");
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validador.texto(nombre, "nombre");
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = Validador.positivo(precio, "precio");
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto)) return false;
        return codigo.equals(((Producto) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }
}
