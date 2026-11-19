package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.PruebaBase;
import org.example.proyectofinalprogramacion2.exception.PuntosInsuficientesException;
import org.example.proyectofinalprogramacion2.model.AcumulacionPuntos;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de puntos: 1 punto por cada $100, vencimiento al año, orden de consumo y anulación por reembolso.
 */
class PuntosServiceTest extends PruebaBase {

    private final PuntosService puntosService = new PuntosService();
    private final CompraService compraService = new CompraService();
    private final CancelacionService cancelacionService = new CancelacionService();

    /** Cliente nuevo sin puntos ni compras. */
    private Cliente clienteNuevo() throws Exception {
        return new AutenticacionService().registrarCliente("Cliente de prueba", "prueba@correo.com", "prueba123");
    }

    private AcumulacionPuntos agregarPuntos(Cliente cliente, int puntos, LocalDate fechaObtencion) {
        AcumulacionPuntos acumulacion = new AcumulacionPuntos(puntos, fechaObtencion, null);
        cliente.getCuentaPuntos().agregarAcumulacion(acumulacion);
        return acumulacion;
    }

    private AcumulacionPuntos ultimaAcumulacion(Cliente cliente) {
        List<AcumulacionPuntos> acumulaciones = cliente.getCuentaPuntos().getAcumulaciones();
        return acumulaciones.get(acumulaciones.size() - 1);
    }

    // Una compra con tarjeta acumula 1 punto por cada $100 pagados.
    @Test
    void testCompraConTarjetaAcumulaUnPuntoPorCada100() throws Exception {
        Cliente ana = cliente(ANA);
        int vigentesAntes = puntosService.getPuntosVigentes(ana, HOY);
        Funcion funcion = funcion("F004");

        Compra compra = compraService.comprarEntradas(ana, funcion, List.of(asiento(funcion, "C", 5)), List.of(),
                List.of(), null, AHORA);

        AcumulacionPuntos acumulacion = ultimaAcumulacion(ana);
        assertEquals(180, acumulacion.getPuntosIniciales());              // $18.000 / 100
        assertSame(compra, acumulacion.getCompraOrigen());
        assertEquals(HOY, acumulacion.getFechaObtencion());
        assertEquals(HOY.plusYears(1), acumulacion.getFechaVencimiento());
        assertEquals(vigentesAntes + 180, puntosService.getPuntosVigentes(ana, HOY));
    }

    // Al redimir se usan primero los puntos más próximos a vencer.
    @Test
    void testRedimirUsaPrimeroLosPuntosProximosAVencer() throws Exception {
        Cliente cliente = clienteNuevo();
        AcumulacionPuntos recientes = agregarPuntos(cliente, 300, HOY.minusMonths(2));
        AcumulacionPuntos proximosAVencer = agregarPuntos(cliente, 500, HOY.minusMonths(10));

        puntosService.redimir(cliente, 400, HOY);

        assertEquals(100, proximosAVencer.getPuntosDisponibles());
        assertEquals(300, recientes.getPuntosDisponibles());
        assertEquals(400, puntosService.getPuntosVigentes(cliente, HOY));
    }

    // Si una acumulación se agota, el resto se toma de la siguiente.
    @Test
    void testRedimirPasaALaSiguienteAcumulacionCuandoUnaSeAgota() throws Exception {
        Cliente cliente = clienteNuevo();
        AcumulacionPuntos recientes = agregarPuntos(cliente, 300, HOY.minusMonths(2));
        AcumulacionPuntos proximosAVencer = agregarPuntos(cliente, 500, HOY.minusMonths(10));

        puntosService.redimir(cliente, 700, HOY);

        assertEquals(0, proximosAVencer.getPuntosDisponibles());
        assertEquals(100, recientes.getPuntosDisponibles());
    }

    // Los puntos vencidos no se usan al redimir.
    @Test
    void testPuntosVencidosNoSeUsan() throws Exception {
        Cliente cliente = clienteNuevo();
        AcumulacionPuntos vencidos = agregarPuntos(cliente, 1_000, HOY.minusMonths(13));
        agregarPuntos(cliente, 300, HOY.minusMonths(1));

        assertEquals(300, puntosService.getPuntosVigentes(cliente, HOY));
        assertEquals(1_000, puntosService.getPuntosVencidos(cliente, HOY));
        assertThrows(PuntosInsuficientesException.class, () -> puntosService.redimir(cliente, 400, HOY));
        assertEquals(1_000, vencidos.getPuntosDisponibles());
    }

    // Los puntos vencen al cumplir un año de obtenidos.
    @Test
    void testPuntosVencenAlCumplirUnAnio() throws Exception {
        Cliente cliente = clienteNuevo();
        AcumulacionPuntos cumpleUnAnio = agregarPuntos(cliente, 200, HOY.minusYears(1));
        AcumulacionPuntos faltaUnDia = agregarPuntos(cliente, 300, HOY.minusYears(1).plusDays(1));

        assertTrue(cumpleUnAnio.estaVencida(HOY));
        assertFalse(faltaUnDia.estaVencida(HOY));
        assertEquals(300, puntosService.getPuntosVigentes(cliente, HOY));
    }

    // Si los puntos no alcanzan, no se descuenta nada.
    @Test
    void testPuntosInsuficientesNoDescuentaNada() throws Exception {
        Cliente cliente = clienteNuevo();
        AcumulacionPuntos primera = agregarPuntos(cliente, 300, HOY.minusMonths(5));
        AcumulacionPuntos segunda = agregarPuntos(cliente, 200, HOY.minusMonths(2));

        assertThrows(PuntosInsuficientesException.class, () -> puntosService.redimir(cliente, 600, HOY));

        assertEquals(300, primera.getPuntosDisponibles());
        assertEquals(200, segunda.getPuntosDisponibles());
    }

    // Un canje de producto sin puntos suficientes no registra ninguna compra.
    @Test
    void testRedimirProductoConPuntosInsuficientesNoRegistraCompra() throws Exception {
        Cliente cliente = clienteNuevo();
        agregarPuntos(cliente, 300, HOY.minusMonths(1));

        assertThrows(PuntosInsuficientesException.class,
                () -> compraService.redimirProducto(cliente, producto("PR01"), AHORA));
        assertTrue(cliente.getCompras().isEmpty());
    }

    // Al reembolsar una compra se anulan sus puntos si siguen sin usar.
    @Test
    void testReembolsoAnulaPuntosSinUsar() throws Exception {
        Cliente ana = cliente(ANA);
        int vigentesAntes = puntosService.getPuntosVigentes(ana, HOY);
        Funcion funcion = funcion("F004");
        Compra compra = compraService.comprarEntradas(ana, funcion, List.of(asiento(funcion, "C", 5)), List.of(),
                List.of(), null, AHORA);
        AcumulacionPuntos generados = ultimaAcumulacion(ana);

        cancelacionService.cancelarCompra(ana, compra, AHORA);

        assertTrue(generados.isAnulada());
        assertEquals(vigentesAntes, puntosService.getPuntosVigentes(ana, HOY));
    }

    // El reembolso se hace aunque los puntos de esa compra ya se hayan usado.
    @Test
    void testReembolsoProcedeAunqueLosPuntosYaSeUsaron() throws Exception {
        Cliente cliente = clienteNuevo();
        new TarjetaService().recargar(administrador(), cliente, 100_000, AHORA);
        Funcion funcion = funcion("F004");
        Compra compra = compraService.comprarEntradas(cliente, funcion,
                List.of(asiento(funcion, "C", 5), asiento(funcion, "C", 6), asiento(funcion, "C", 7)),
                List.of(), List.of(), null, AHORA);                      // $54.000 -> 540 puntos
        AcumulacionPuntos generados = ultimaAcumulacion(cliente);
        compraService.redimirProducto(cliente, producto("PR02"), AHORA);  // usa 400 de esos puntos
        int acumulacionesTrasCanje = cliente.getCuentaPuntos().getAcumulaciones().size();

        double devuelto = cancelacionService.cancelarCompra(cliente, compra, AHORA);

        assertEquals(54_000, devuelto, 0.001);
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
        assertFalse(generados.isAnulada());
        assertEquals(140, generados.getPuntosDisponibles());
        assertEquals(acumulacionesTrasCanje, cliente.getCuentaPuntos().getAcumulaciones().size(),
                "Un canje con puntos no genera puntos");
    }
}
