package org.example.proyectofinalprogramacion2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tarjeta virtual con la que el cliente paga sus compras.
 */
public class TarjetaVirtual {

    private String numero;
    private double saldo;
    private EstadoTarjeta estado;
    private final List<MovimientoTarjeta> movimientos;

    public TarjetaVirtual(String numero, double saldoInicial) {
        setNumero(numero);
        setSaldo(saldoInicial);
        this.estado = EstadoTarjeta.ACTIVA;
        this.movimientos = new ArrayList<>();
    }

    /** Indica si la tarjeta puede usarse para pagar. */
    public boolean estaActiva() {
        return estado == EstadoTarjeta.ACTIVA;
    }

    public void agregarMovimiento(MovimientoTarjeta movimiento) {
        movimientos.add(Validador.noNulo(movimiento, "movimiento"));
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = Validador.texto(numero, "número de tarjeta");
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = Validador.noNegativo(saldo, "saldo");
    }

    public EstadoTarjeta getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarjeta estado) {
        this.estado = Validador.noNulo(estado, "estado");
    }

    public List<MovimientoTarjeta> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }

    @Override
    public String toString() {
        return "TarjetaVirtual{numero='" + numero + "', saldo=" + saldo + ", estado=" + estado
                + ", movimientos=" + movimientos.size() + "}";
    }
}
