package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.observer.ObservadorFuncion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cliente del cine. Tiene una tarjeta virtual, una cuenta de puntos, su historial de compras
 * y sus notificaciones. Observa las funciones en las que tiene compras pagadas.
 */
// LSP: puede usarse donde se espere un Usuario (inicio de sesión, listados) sin romper su contrato.
public class Cliente extends Usuario implements ObservadorFuncion {

    private TarjetaVirtual tarjeta;
    private final CuentaPuntos cuentaPuntos;
    private final List<Compra> compras;
    private final List<String> notificaciones;

    public Cliente(String nombre, String correo, String clave, TarjetaVirtual tarjeta) {
        super(nombre, correo, clave);
        setTarjeta(tarjeta);
        this.cuentaPuntos = new CuentaPuntos();
        this.compras = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
    }

    public void agregarCompra(Compra compra) {
        compras.add(Validador.noNulo(compra, "compra"));
    }

    /**
     * Indica si el cliente tiene alguna compra PAGADA para la función.
     */
    public boolean tieneCompraPagadaEn(Funcion funcion) {
        for (Compra compra : compras) {
            if (funcion.equals(compra.getFuncion()) && compra.getEstado() == EstadoCompra.PAGADA) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actualizar(Funcion funcion, String mensaje) {
        notificaciones.add(mensaje);
    }

    public List<String> getNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }

    @Override
    public String getRol() {
        return "CLIENTE";
    }

    public TarjetaVirtual getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(TarjetaVirtual tarjeta) {
        this.tarjeta = Validador.noNulo(tarjeta, "tarjeta");
    }

    public CuentaPuntos getCuentaPuntos() {
        return cuentaPuntos;
    }

    public List<Compra> getCompras() {
        return Collections.unmodifiableList(compras);
    }

    @Override
    public String toString() {
        return "Cliente{nombre='" + getNombre() + "', correo='" + getCorreo()
                + "', tarjeta=" + tarjeta.getNumero() + ", compras=" + compras.size() + "}";
    }
}
