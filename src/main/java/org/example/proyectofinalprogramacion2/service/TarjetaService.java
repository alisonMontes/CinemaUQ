package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.exception.AccesoDenegadoException;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.EstadoTarjeta;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.model.TarjetaVirtual;
import org.example.proyectofinalprogramacion2.model.TipoMovimiento;
import org.example.proyectofinalprogramacion2.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Consulta de la tarjeta (cliente) y recarga, activación y bloqueo (solo administrador).
 * Los pagos y reembolsos se hacen con PagoTarjetaAdapter desde la fachada.
 */
// SRP: solo administra el saldo y el estado de las tarjetas fuera del proceso de compra.
public class TarjetaService {

    public double consultarSaldo(Cliente cliente) {
        return cliente.getTarjeta().getSaldo();
    }

    public List<MovimientoTarjeta> consultarMovimientos(Cliente cliente) {
        return cliente.getTarjeta().getMovimientos();
    }

    /**
     * Recarga la tarjeta del cliente. Se permite aunque esté bloqueada.
     */
    public void recargar(Usuario actor, Cliente cliente, double monto, LocalDateTime ahora)
            throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto de la recarga debe ser mayor que cero");
        }
        TarjetaVirtual tarjeta = cliente.getTarjeta();
        tarjeta.setSaldo(tarjeta.getSaldo() + monto);
        tarjeta.agregarMovimiento(new MovimientoTarjeta(TipoMovimiento.RECARGA, ahora, monto,
                "Recarga realizada por " + actor.getNombre()));
    }

    public void activar(Usuario actor, Cliente cliente) throws AccesoDenegadoException {
        cambiarEstado(actor, cliente, EstadoTarjeta.ACTIVA);
    }

    public void bloquear(Usuario actor, Cliente cliente) throws AccesoDenegadoException {
        cambiarEstado(actor, cliente, EstadoTarjeta.BLOQUEADA);
    }

    private void cambiarEstado(Usuario actor, Cliente cliente, EstadoTarjeta estado) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        cliente.getTarjeta().setEstado(estado);
    }
}
