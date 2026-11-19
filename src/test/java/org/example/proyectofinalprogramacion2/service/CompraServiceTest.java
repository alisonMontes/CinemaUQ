package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.PruebaBase;
import org.example.proyectofinalprogramacion2.exception.AsientoNoDisponibleException;
import org.example.proyectofinalprogramacion2.exception.SaldoInsuficienteException;
import org.example.proyectofinalprogramacion2.exception.TarjetaInactivaException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.model.TipoMovimiento;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de compra con tarjeta: cobro, ocupación de asientos y validaciones de saldo, tarjeta y asientos.
 */
class CompraServiceTest extends PruebaBase {

    private final CompraService compraService = new CompraService();

    // Una compra exitosa descuenta el saldo y ocupa el asiento.
    @Test
    void testCompraExitosaDescuentaSaldoYOcupaAsiento() throws Exception {
        Cliente ana = cliente(ANA);
        Funcion funcion = funcion("F004");
        Asiento asiento = asiento(funcion, "C", 5);
        double saldoInicial = ana.getTarjeta().getSaldo();

        Compra compra = compraService.comprarEntradas(ana, funcion, List.of(asiento), List.of(), List.of(),
                null, AHORA);

        assertEquals(EstadoCompra.PAGADA, compra.getEstado());
        assertEquals(18_000, compra.getTotal(), 0.001);
        assertEquals(saldoInicial - 18_000, ana.getTarjeta().getSaldo(), 0.001);
        assertFalse(funcion.estaDisponible(asiento));
        assertTrue(ana.getCompras().contains(compra));
        assertTrue(cinema.getCompras().contains(compra));
        List<MovimientoTarjeta> movimientos = ana.getTarjeta().getMovimientos();
        MovimientoTarjeta ultimo = movimientos.get(movimientos.size() - 1);
        assertEquals(TipoMovimiento.PAGO, ultimo.getTipo());
        assertEquals(18_000, ultimo.getMonto(), 0.001);
    }

    // Sin saldo suficiente no se cobra ni se ocupa ningún asiento.
    @Test
    void testCompraConSaldoInsuficienteNoCobraNiOcupaAsientos() {
        Cliente carlos = cliente(CARLOS);                       // saldo $114.800
        Funcion funcion = funcion("F003");                      // $20.000 por entrada
        List<Asiento> asientos = new ArrayList<>();
        for (int numero = 1; numero <= 6; numero++) {
            asientos.add(asiento(funcion, "C", numero));
        }
        asientos.add(asiento(funcion, "D", 1));                 // 7 entradas = $140.000
        double saldoInicial = carlos.getTarjeta().getSaldo();
        int comprasIniciales = carlos.getCompras().size();
        int movimientosIniciales = carlos.getTarjeta().getMovimientos().size();

        assertThrows(SaldoInsuficienteException.class, () -> compraService.comprarEntradas(carlos, funcion,
                asientos, List.of(), List.of(), null, AHORA));

        assertEquals(saldoInicial, carlos.getTarjeta().getSaldo(), 0.001);
        assertEquals(comprasIniciales, carlos.getCompras().size());
        assertEquals(movimientosIniciales, carlos.getTarjeta().getMovimientos().size());
        for (Asiento asiento : asientos) {
            assertTrue(funcion.estaDisponible(asiento), "El asiento " + asiento + " no debe quedar ocupado");
        }
    }

    // Con la tarjeta bloqueada la compra lanza una excepción.
    @Test
    void testCompraConTarjetaBloqueadaLanzaExcepcion() {
        Cliente laura = cliente(LAURA);                         // tarjeta BLOQUEADA con saldo
        Funcion funcion = funcion("F004");
        Asiento asiento = asiento(funcion, "C", 5);
        double saldoInicial = laura.getTarjeta().getSaldo();

        assertThrows(TarjetaInactivaException.class, () -> compraService.comprarEntradas(laura, funcion,
                List.of(asiento), List.of(), List.of(), null, AHORA));

        assertEquals(saldoInicial, laura.getTarjeta().getSaldo(), 0.001);
        assertTrue(funcion.estaDisponible(asiento));
    }

    // Un asiento ya vendido no se vende otra vez en la misma función.
    @Test
    void testAsientoNoSeVendeDosVecesEnLaMismaFuncion() throws Exception {
        Funcion funcion = funcion("F004");
        Asiento asiento = asiento(funcion, "C", 5);
        compraService.comprarEntradas(cliente(ANA), funcion, List.of(asiento), List.of(), List.of(), null, AHORA);
        Cliente carlos = cliente(CARLOS);
        double saldoCarlos = carlos.getTarjeta().getSaldo();

        assertThrows(AsientoNoDisponibleException.class, () -> compraService.comprarEntradas(carlos, funcion,
                List.of(asiento), List.of(), List.of(), null, AHORA));

        assertEquals(saldoCarlos, carlos.getTarjeta().getSaldo(), 0.001);
        assertEquals(1, funcion.getAsientosOcupados().stream().filter(asiento::equals).count());
    }

    // El mismo asiento sí se puede vender en otra función de la misma sala.
    @Test
    void testMismoAsientoSePuedeVenderEnOtraFuncionDeLaMismaSala() throws Exception {
        Funcion f004 = funcion("F004");
        Funcion f006 = funcion("F006");                         // también en Sala 1
        Asiento asiento = asiento(f004, "C", 5);

        compraService.comprarEntradas(cliente(ANA), f004, List.of(asiento), List.of(), List.of(), null, AHORA);
        compraService.comprarEntradas(cliente(CARLOS), f006, List.of(asiento), List.of(), List.of(), null, AHORA);

        assertFalse(f004.estaDisponible(asiento));
        assertFalse(f006.estaDisponible(asiento));
    }
}
