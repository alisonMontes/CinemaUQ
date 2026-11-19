package org.example.proyectofinalprogramacion2.adapter;

import org.example.proyectofinalprogramacion2.exception.SaldoInsuficienteException;
import org.example.proyectofinalprogramacion2.exception.TarjetaInactivaException;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.model.TarjetaVirtual;
import org.example.proyectofinalprogramacion2.model.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Adapter: adapta TarjetaVirtual (saldo, estado y movimientos) a la interfaz MedioPago.
 */
// SRP: solo traduce las operaciones de pago a cambios de saldo y movimientos de la tarjeta.
public class PagoTarjetaAdapter implements MedioPago {

    private final TarjetaVirtual tarjeta;

    public PagoTarjetaAdapter(TarjetaVirtual tarjeta) {
        this.tarjeta = Objects.requireNonNull(tarjeta, "La tarjeta es obligatoria");
    }

    /**
     * Verifica que la tarjeta esté ACTIVA y tenga saldo suficiente.
     *
     * @throws TarjetaInactivaException si la tarjeta está bloqueada
     * @throws SaldoInsuficienteException si el saldo no alcanza
     */
    @Override
    public void validarPago(double monto) throws TarjetaInactivaException, SaldoInsuficienteException {
        if (!tarjeta.estaActiva()) {
            throw new TarjetaInactivaException("La tarjeta " + tarjeta.getNumero() + " está bloqueada");
        }
        if (tarjeta.getSaldo() < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente: disponible $" + tarjeta.getSaldo()
                    + ", requerido $" + monto);
        }
    }

    /** Valida, descuenta el saldo y registra un movimiento PAGO. */
    @Override
    public void cobrar(double monto, String concepto, LocalDateTime fecha)
            throws TarjetaInactivaException, SaldoInsuficienteException {
        validarPago(monto);
        tarjeta.setSaldo(tarjeta.getSaldo() - monto);
        tarjeta.agregarMovimiento(new MovimientoTarjeta(TipoMovimiento.PAGO, fecha, monto, concepto));
    }

    /** Suma el monto al saldo (aunque la tarjeta esté bloqueada) y registra un movimiento REEMBOLSO. */
    @Override
    public void reembolsar(double monto, String concepto, LocalDateTime fecha) {
        tarjeta.setSaldo(tarjeta.getSaldo() + monto);
        tarjeta.agregarMovimiento(new MovimientoTarjeta(TipoMovimiento.REEMBOLSO, fecha, monto, concepto));
    }

    @Override
    public String getDescripcion() {
        return "Tarjeta virtual " + tarjeta.getNumero();
    }

    public TarjetaVirtual getTarjeta() {
        return tarjeta;
    }

    @Override
    public String toString() {
        return getDescripcion();
    }
}
