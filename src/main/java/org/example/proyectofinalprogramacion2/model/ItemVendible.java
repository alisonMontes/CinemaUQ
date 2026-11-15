package org.example.proyectofinalprogramacion2.model;

/**
 * Todo lo que puede incluirse en una compra: entradas, productos y combos.
 */
// ISP: solo nombre y precio; cada ítem agrega lo suyo sin obligar a los demás.
public interface ItemVendible {

    String getNombre();

    double getPrecio();
}
