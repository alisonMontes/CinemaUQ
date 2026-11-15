package org.example.proyectofinalprogramacion2.model;

/**
 * Entrada para un asiento de una función. Su precio es el precio base de la función.
 */
// LSP: EntradaGeneral y EntradaAccesible la sustituyen; la compra solo usa getNombre() y getPrecio().
public abstract class Entrada implements ItemVendible {

    private Funcion funcion;
    private Asiento asiento;

    protected Entrada(Funcion funcion, Asiento asiento) {
        setFuncion(funcion);
        setAsiento(asiento);
    }

    /**
     * Tipo de entrada mostrado en el nombre y en los reportes.
     */
    public abstract String getTipo();

    @Override
    public String getNombre() {
        return "Entrada " + getTipo() + " " + asiento.getCodigo() + " - " + funcion.getPelicula().getTitulo();
    }

    @Override
    public double getPrecio() {
        return funcion.getPrecioBase();
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = Validador.noNulo(funcion, "función");
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = Validador.noNulo(asiento, "asiento");
    }

    @Override
    public String toString() {
        return getNombre() + " ($" + getPrecio() + ")";
    }
}
