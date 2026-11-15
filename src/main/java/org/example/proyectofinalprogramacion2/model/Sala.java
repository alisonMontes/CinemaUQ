package org.example.proyectofinalprogramacion2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Sala del cine con su distribución de asientos.
 */
public class Sala {

    private String codigo;
    private String nombre;
    private final List<Asiento> asientos;

    public Sala(String codigo, String nombre) {
        setCodigo(codigo);
        setNombre(nombre);
        this.asientos = new ArrayList<>();
    }

    public void agregarAsiento(Asiento asiento) {
        Validador.noNulo(asiento, "asiento");
        if (asientos.contains(asiento)) {
            throw new IllegalArgumentException("El asiento " + asiento.getCodigo() + " ya existe en la sala");
        }
        asientos.add(asiento);
    }

    public boolean eliminarAsiento(Asiento asiento) {
        return asientos.remove(asiento);
    }

    public Asiento buscarAsiento(String fila, int numero) {
        for (Asiento asiento : asientos) {
            if (asiento.getFila().equalsIgnoreCase(fila) && asiento.getNumero() == numero) {
                return asiento;
            }
        }
        return null;
    }

    public boolean contieneAsiento(Asiento asiento) {
        return asientos.contains(asiento);
    }

    public int getCapacidad() {
        return asientos.size();
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

    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sala)) return false;
        return codigo.equals(((Sala) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return nombre + " (" + asientos.size() + " asientos)";
    }
}
