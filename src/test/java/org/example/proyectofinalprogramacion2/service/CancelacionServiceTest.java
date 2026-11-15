package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.PruebaBase;
import org.example.proyectofinalprogramacion2.exception.AccesoDenegadoException;
import org.example.proyectofinalprogramacion2.exception.CancelacionNoPermitidaException;
import org.example.proyectofinalprogramacion2.exception.ReembolsoYaEjecutadoException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoCompleto;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoParcial;
import org.example.proyectofinalprogramacion2.strategy.SinReembolso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CancelacionServiceTest extends PruebaBase {

    private final CompraService compraService = new CompraService();
    private final CancelacionService cancelacionService = new CancelacionService();

    /** Ana compra el asiento C5 de F004 (12/03 20:00) por $18.000. */
    private Compra comprarEnF004() throws Exception {
        Funcion funcion = funcion("F004");
        return compraService.comprarEntradas(cliente(ANA), funcion, List.of(asiento(funcion, "C", 5)),
                List.of(), List.of(), null, AHORA);
    }

    private LocalDateTime inicioF004() {
        return funcion("F004").getFechaHora();
    }

    @Test
    void testReembolsoCompletoConMasDe24Horas() throws Exception {
        Compra compra = comprarEnF004();
        Cliente ana = cliente(ANA);
        double saldoTrasComprar = ana.getTarjeta().getSaldo();
        LocalDateTime ahora = inicioF004().minusHours(24).minusMinutes(1);

        double devuelto = cancelacionService.cancelarCompra(ana, compra, ahora);

        assertEquals(18_000, devuelto, 0.001);
        assertEquals(saldoTrasComprar + 18_000, ana.getTarjeta().getSaldo(), 0.001);
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
        assertTrue(funcion("F004").estaDisponible(compra.getEntradas().get(0).getAsiento()));
    }

    @Test
    void testReembolsoParcialExactamente24Horas() throws Exception {
        Compra compra = comprarEnF004();

        double devuelto = cancelacionService.cancelarCompra(cliente(ANA), compra, inicioF004().minusHours(24));

        assertEquals(18_000 * 0.8, devuelto, 0.001);
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
    }

    @Test
    void testReembolsoParcialExactamente2Horas() throws Exception {
        Compra compra = comprarEnF004();

        double devuelto = cancelacionService.cancelarCompra(cliente(ANA), compra, inicioF004().minusHours(2));

        assertEquals(18_000 * 0.8, devuelto, 0.001);
    }

    @Test
    void testCancelacionNoPermitidaConMenosDe2Horas() throws Exception {
        Compra compra = comprarEnF004();
        Cliente ana = cliente(ANA);
        double saldoTrasComprar = ana.getTarjeta().getSaldo();
        LocalDateTime ahora = inicioF004().minusHours(2).plusMinutes(1);

        assertThrows(CancelacionNoPermitidaException.class,
                () -> cancelacionService.cancelarCompra(ana, compra, ahora));

        assertEquals(EstadoCompra.PAGADA, compra.getEstado());
        assertEquals(saldoTrasComprar, ana.getTarjeta().getSaldo(), 0.001);
        assertFalse(funcion("F004").estaDisponible(compra.getEntradas().get(0).getAsiento()));
    }

    @Test
    void testConsultarPoliticaRespetaLosLimitesDe24Y2Horas() throws Exception {
        Compra compra = comprarEnF004();
        LocalDateTime inicio = inicioF004();

        assertInstanceOf(ReembolsoCompleto.class,
                cancelacionService.consultarPolitica(compra, inicio.minusHours(24).minusMinutes(1)));
        assertInstanceOf(ReembolsoParcial.class, cancelacionService.consultarPolitica(compra, inicio.minusHours(24)));
        assertInstanceOf(ReembolsoParcial.class, cancelacionService.consultarPolitica(compra, inicio.minusHours(2)));
        assertInstanceOf(SinReembolso.class,
                cancelacionService.consultarPolitica(compra, inicio.minusHours(2).plusMinutes(1)));
    }

    @Test
    void testNoSeReembolsaDosVecesLaMismaCompra() throws Exception {
        Compra compra = comprarEnF004();
        Cliente ana = cliente(ANA);
        cancelacionService.cancelarCompra(ana, compra, AHORA);
        double saldoTrasReembolso = ana.getTarjeta().getSaldo();

        assertThrows(ReembolsoYaEjecutadoException.class,
                () -> cancelacionService.cancelarCompra(ana, compra, AHORA));

        assertEquals(saldoTrasReembolso, ana.getTarjeta().getSaldo(), 0.001);
        assertEquals(18_000, compra.getMontoReembolsado(), 0.001);
    }

    @Test
    void testCancelarFuncionReembolsaCienPorCientoYNotifica() throws Exception {
        Funcion f002 = funcion("F002");                          // hoy 20:00: un cliente solo recibiría el 80 %
        Cliente carlos = cliente(CARLOS);
        Compra compraCarlos = carlos.getCompras().get(0);
        double saldoInicial = carlos.getTarjeta().getSaldo();
        int notificacionesIniciales = carlos.getNotificaciones().size();

        List<Compra> reembolsadas = cancelacionService.cancelarFuncion(administrador(), f002, AHORA);

        assertEquals(EstadoFuncion.CANCELADA, f002.getEstado());
        assertEquals(List.of(compraCarlos), reembolsadas);
        assertEquals(compraCarlos.getTotal(), compraCarlos.getMontoReembolsado(), 0.001);
        assertEquals(saldoInicial + compraCarlos.getTotal(), carlos.getTarjeta().getSaldo(), 0.001);
        assertEquals(notificacionesIniciales + 1, carlos.getNotificaciones().size());
        assertTrue(carlos.getNotificaciones().get(carlos.getNotificaciones().size() - 1).contains("cancelada"));
    }

    @Test
    void testClienteNoPuedeCancelarFuncion() {
        assertThrows(AccesoDenegadoException.class,
                () -> cancelacionService.cancelarFuncion(cliente(ANA), funcion("F002"), AHORA));
        assertEquals(EstadoFuncion.PROGRAMADA, funcion("F002").getEstado());
    }

    @Test
    void testClienteNoPuedeCancelarCompraAjena() throws Exception {
        Compra compraDeAna = comprarEnF004();

        assertThrows(AccesoDenegadoException.class,
                () -> cancelacionService.cancelarCompra(cliente(CARLOS), compraDeAna, AHORA));
        assertEquals(EstadoCompra.PAGADA, compraDeAna.getEstado());
    }

    @Test
    void testCompraPagadaConPuntosNoSeCancela() {
        Cliente ana = cliente(ANA);
        Compra canje = ana.getCompras().stream().filter(Compra::isPagadaConPuntos).findFirst().orElseThrow();
        Asiento asiento = canje.getEntradas().get(0).getAsiento();

        assertThrows(CancelacionNoPermitidaException.class, () -> cancelacionService.cancelarCompra(ana, canje, AHORA));
        assertEquals(EstadoCompra.PAGADA, canje.getEstado());
        assertFalse(canje.getFuncion().estaDisponible(asiento));
    }
}
