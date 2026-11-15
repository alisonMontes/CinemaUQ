package org.example.proyectofinalprogramacion2.decorator;

import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.model.Promocion;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Decorator: envuelve un ItemVendible y le aplica el descuento de una promoción
 * si está vigente en la fecha indicada. El ítem original no se modifica.
 */
// SRP: solo agrega el descuento al precio del ítem envuelto.
// OCP: se cambia el precio sin modificar Entrada, Producto ni Combo.
public class PromocionDecorator implements ItemVendible {

    private final ItemVendible item;
    private final Promocion promocion;
    private final LocalDate fecha;

    public PromocionDecorator(ItemVendible item, Promocion promocion, LocalDate fecha) {
        this.item = Objects.requireNonNull(item, "El ítem es obligatorio");
        this.promocion = Objects.requireNonNull(promocion, "La promoción es obligatoria");
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria");
    }

    public boolean aplicaDescuento() {
        return promocion.estaVigente(fecha);
    }

    @Override
    public String getNombre() {
        return aplicaDescuento() ? item.getNombre() + " [" + promocion.getNombre() + "]" : item.getNombre();
    }

    @Override
    public double getPrecio() {
        double precio = item.getPrecio();
        return aplicaDescuento() ? precio - promocion.calcularDescuento(precio) : precio;
    }

    public ItemVendible getItem() {
        return item;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    @Override
    public String toString() {
        return getNombre() + " ($" + getPrecio() + ")";
    }
}
