package org.example.proyectofinalprogramacion2.model;

/**
 * Todo lo que puede incluirse en una compra: entradas, productos y combos.
 * Es el componente común del Composite (Combo) y del Decorator (PromocionDecorator).
 */
// ISP: solo nombre y precio; cada ítem agrega lo suyo sin obligar a los demás.
public interface ItemVendible {

    String getNombre();

    double getPrecio();
}
