package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.PruebaBase;
import org.example.proyectofinalprogramacion2.exception.AccesoDenegadoException;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.EstadoTarjeta;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.model.TipoMovimiento;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de la tarjeta virtual: recarga, activación y bloqueo solo por el administrador.
 */
class TarjetaServiceTest extends PruebaBase {

    private final TarjetaService tarjetaService = new TarjetaService();

    // La recarga del administrador aumenta el saldo y registra el movimiento.
    @Test
    void testRecargaPorAdministradorAumentaSaldoYRegistraMovimiento() throws Exception {
        Cliente carlos = cliente(CARLOS);
        double saldoInicial = carlos.getTarjeta().getSaldo();

        tarjetaService.recargar(administrador(), carlos, 25_000, AHORA);

        assertEquals(saldoInicial + 25_000, carlos.getTarjeta().getSaldo(), 0.001);
        List<MovimientoTarjeta> movimientos = carlos.getTarjeta().getMovimientos();
        MovimientoTarjeta ultimo = movimientos.get(movimientos.size() - 1);
        assertEquals(TipoMovimiento.RECARGA, ultimo.getTipo());
        assertEquals(25_000, ultimo.getMonto(), 0.001);
        assertEquals(AHORA, ultimo.getFecha());
    }

    // Un cliente no puede recargar: se lanza AccesoDenegadoException.
    @Test
    void testRecargaPorClienteLanzaAccesoDenegado() {
        Cliente carlos = cliente(CARLOS);
        double saldoInicial = carlos.getTarjeta().getSaldo();
        int movimientosIniciales = carlos.getTarjeta().getMovimientos().size();

        assertThrows(AccesoDenegadoException.class,
                () -> tarjetaService.recargar(cliente(ANA), carlos, 25_000, AHORA));
        assertThrows(AccesoDenegadoException.class,
                () -> tarjetaService.recargar(carlos, carlos, 25_000, AHORA));

        assertEquals(saldoInicial, carlos.getTarjeta().getSaldo(), 0.001);
        assertEquals(movimientosIniciales, carlos.getTarjeta().getMovimientos().size());
    }

    // Un cliente no puede bloquear una tarjeta.
    @Test
    void testBloqueoPorClienteLanzaAccesoDenegado() {
        Cliente carlos = cliente(CARLOS);

        assertThrows(AccesoDenegadoException.class, () -> tarjetaService.bloquear(cliente(ANA), carlos));

        assertEquals(EstadoTarjeta.ACTIVA, carlos.getTarjeta().getEstado());
    }

    // Un cliente no puede activar una tarjeta.
    @Test
    void testActivacionPorClienteLanzaAccesoDenegado() {
        Cliente laura = cliente(LAURA);

        assertThrows(AccesoDenegadoException.class, () -> tarjetaService.activar(laura, laura));

        assertEquals(EstadoTarjeta.BLOQUEADA, laura.getTarjeta().getEstado());
    }

    // El administrador puede bloquear y volver a activar una tarjeta.
    @Test
    void testBloqueoYActivacionPorAdministrador() throws Exception {
        Cliente carlos = cliente(CARLOS);

        tarjetaService.bloquear(administrador(), carlos);
        assertEquals(EstadoTarjeta.BLOQUEADA, carlos.getTarjeta().getEstado());

        tarjetaService.activar(administrador(), carlos);
        assertEquals(EstadoTarjeta.ACTIVA, carlos.getTarjeta().getEstado());
    }
}
