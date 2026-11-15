package org.example.proyectofinalprogramacion2.controller;

import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.model.Producto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lo que el cliente va eligiendo en las pestañas antes de pagar: función, asientos, productos y combos.
 * Es solo estado de la interfaz; la compra la arma y valida CompraService al pagar.
 */
public class CarritoCompra {

    private Funcion funcion;
    private final List<Asiento> asientos = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();
    private final List<Combo> combos = new ArrayList<>();

    /**
     * Cambia la función elegida; si es otra, se descartan los asientos que se habían elegido.
     */
    public void setFuncion(Funcion funcion) {
        if (this.funcion == null || !this.funcion.equals(funcion)) {
            asientos.clear();
        }
        this.funcion = funcion;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    /**
     * Selecciona el asiento si no estaba elegido o lo quita si ya lo estaba.
     */
    public void alternarAsiento(Asiento asiento) {
        if (!asientos.remove(asiento)) {
            asientos.add(asiento);
        }
    }

    public boolean contieneAsiento(Asiento asiento) {
        return asientos.contains(asiento);
    }

    /**
     * Descarta los asientos elegidos que ya no están en la lista de disponibles.
     */
    public void conservarSolo(List<Asiento> disponibles) {
        asientos.retainAll(disponibles);
    }

    public void quitarAsiento(Asiento asiento) {
        asientos.remove(asiento);
    }

    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public void agregarCombo(Combo combo) {
        combos.add(combo);
    }

    public void quitarItem(ItemVendible item) {
        if (item instanceof Producto) {
            productos.remove(item);
        } else if (item instanceof Combo) {
            combos.remove(item);
        }
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public List<Combo> getCombos() {
        return Collections.unmodifiableList(combos);
    }

    /**
     * Productos y combos juntos, para mostrarlos en una sola tabla.
     */
    public List<ItemVendible> getItemsConfiteria() {
        List<ItemVendible> items = new ArrayList<>(productos);
        items.addAll(combos);
        return items;
    }

    /**
     * Deja el carrito vacío después de pagar.
     */
    public void vaciar() {
        funcion = null;
        asientos.clear();
        productos.clear();
        combos.clear();
    }

    @Override
    public String toString() {
        return "CarritoCompra{funcion=" + (funcion == null ? "-" : funcion.getCodigo())
                + ", asientos=" + asientos.size() + ", productos=" + productos.size()
                + ", combos=" + combos.size() + "}";
    }
}
