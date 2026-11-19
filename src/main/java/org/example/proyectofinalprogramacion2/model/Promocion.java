package org.example.proyectofinalprogramacion2.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Descuento porcentual sobre el total de una compra, válido entre dos fechas (inclusive).
 */
public class Promocion {

    private String codigo;
    private String nombre;
    private double porcentajeDescuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Promocion(String codigo, String nombre, double porcentajeDescuento,
                     LocalDate fechaInicio, LocalDate fechaFin) {
        setCodigo(codigo);
        setNombre(nombre);
        setPorcentajeDescuento(porcentajeDescuento);
        this.fechaInicio = Validador.noNulo(fechaInicio, "fecha de inicio");
        setFechaFin(fechaFin);
    }

    /** Indica si la fecha está entre el inicio y el fin de la promoción (ambos inclusive). */
    public boolean estaVigente(LocalDate hoy) {
        return !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin);
    }

    /** Valor a descontar del monto según el porcentaje de la promoción. */
    public double calcularDescuento(double monto) {
        return monto * porcentajeDescuento / 100;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = Validador.texto(codigo, "código");
    }

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
        if (porcentajeDescuento <= 0 || porcentajeDescuento >= 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        Validador.noNulo(fechaInicio, "fecha de inicio");
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        Validador.noNulo(fechaFin, "fecha de fin");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        this.fechaFin = fechaFin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Promocion)) return false;
        return codigo.equals(((Promocion) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return nombre + " (" + porcentajeDescuento + "%, " + fechaInicio + " a " + fechaFin + ")";
    }
}
