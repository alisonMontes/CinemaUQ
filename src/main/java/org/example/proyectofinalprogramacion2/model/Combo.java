package org.example.proyectofinalprogramacion2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Composite: un combo es un ItemVendible compuesto por otros ItemVendible (productos u otros combos).
 * Su precio se calcula a partir de sus componentes, restando el porcentaje de descuento del combo.
 */
// OCP: acepta cualquier ItemVendible nuevo como componente sin modificar esta clase.
public class Combo implements ItemVendible {

    private String codigo;
    private String nombre;
    private double porcentajeDescuento;
    private final List<ItemVendible> componentes;

    public Combo(String codigo, String nombre, double porcentajeDescuento) {
        setCodigo(codigo);
        setNombre(nombre);
        setPorcentajeDescuento(porcentajeDescuento);
        this.componentes = new ArrayList<>();
    }

    /** Agrega un producto u otro combo; no acepta entradas ni el mismo combo. */
    public void agregarComponente(ItemVendible componente) {
        Validador.noNulo(componente, "componente");
        if (componente == this) {
            throw new IllegalArgumentException("Un combo no puede contenerse a sí mismo");
        }
        if (componente instanceof Entrada) {
            throw new IllegalArgumentException("Un combo solo contiene productos u otros combos");
        }
        componentes.add(componente);
    }

    public boolean eliminarComponente(ItemVendible componente) {
        return componentes.remove(componente);
    }

    /**
     * Suma de los precios de los componentes comprados por separado.
     */
    public double getPrecioRegular() {
        double total = 0;
        for (ItemVendible componente : componentes) {
            total += componente.getPrecio();
        }
        return total;
    }

    /**
     * Precio del combo: precio regular menos el descuento del combo.
     */
    @Override
    public double getPrecio() {
        return getPrecioRegular() * (100 - porcentajeDescuento) / 100;
    }

    /** Diferencia entre el precio regular y el precio del combo. */
    public double getAhorro() {
        return getPrecioRegular() - getPrecio();
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

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento >= 100) {
            throw new IllegalArgumentException("El descuento del combo debe estar entre 0 y 100 (sin incluir 100)");
        }
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public List<ItemVendible> getComponentes() {
        return Collections.unmodifiableList(componentes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Combo)) return false;
        return codigo.equals(((Combo) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return nombre + " ($" + getPrecio() + ", " + componentes.size() + " componentes, -"
                + porcentajeDescuento + "%)";
    }
}
