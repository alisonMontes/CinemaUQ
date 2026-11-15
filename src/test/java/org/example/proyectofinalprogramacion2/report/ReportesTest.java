package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.PruebaBase;
import org.example.proyectofinalprogramacion2.exception.ReporteException;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.model.TipoMovimiento;
import org.example.proyectofinalprogramacion2.service.CompraService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportesTest extends PruebaBase {

    private final GeneradorReportes generador = new GeneradorReportes();

    /**
     * Ingreso neto calculado de forma independiente: pagos con tarjeta menos reembolsos.
     */
    private double pagosMenosReembolsos() {
        double total = 0;
        for (Cliente cliente : cinema.getClientes()) {
            for (MovimientoTarjeta movimiento : cliente.getTarjeta().getMovimientos()) {
                if (movimiento.getTipo() == TipoMovimiento.PAGO) {
                    total += movimiento.getMonto();
                } else if (movimiento.getTipo() == TipoMovimiento.REEMBOLSO) {
                    total -= movimiento.getMonto();
                }
            }
        }
        return total;
    }

    @Test
    void testReporteVentasSumaLoCorrectoEnElPeriodo() throws Exception {
        // Compras del día: 56.400 + 26.100 + 35.200 + 30.500 (reembolsada al 100 %) + 23.000.
        assertEquals(140_700, pagosMenosReembolsos(), 0.001);

        String texto = generador.obtenerReporte(TipoReporte.VENTAS).generarTexto(HOY, HOY, AHORA);

        assertTrue(texto.contains("Total ingresos netos: $140.700"), texto);
        assertTrue(texto.contains("Reembolsos descontados: $30.500"), texto);
        assertTrue(texto.contains("Registros encontrados: 1"), texto);
        assertTrue(texto.contains("Periodo consultado: desde 10/03/2026 hasta 10/03/2026"), texto);
        assertTrue(texto.contains("Fecha de generación: 10/03/2026 10:00"), texto);
    }

    @Test
    void testReporteVentasSoloCuentaLasComprasDelPeriodo() throws Exception {
        Funcion funcion = funcion("F004");
        new CompraService().comprarEntradas(cliente(ANA), funcion, List.of(asiento(funcion, "C", 5)), List.of(),
                List.of(), null, AHORA.plusDays(1));                 // compra del día siguiente: $18.000
        ReporteExportable ventas = generador.obtenerReporte(TipoReporte.VENTAS);

        assertTrue(ventas.generarTexto(HOY, HOY, AHORA).contains("Total ingresos netos: $140.700"));
        assertTrue(ventas.generarTexto(HOY.plusDays(1), HOY.plusDays(1), AHORA)
                .contains("Total ingresos netos: $18.000"));
        String dosDias = ventas.generarTexto(HOY, HOY.plusDays(1), AHORA);
        assertTrue(dosDias.contains("Total ingresos netos: $158.700"), dosDias);
        assertTrue(dosDias.contains("Registros encontrados: 2"), dosDias);
    }

    @Test
    void testReporteVentasSinRegistrosLoIndica() throws Exception {
        LocalDate desde = LocalDate.of(2025, 1, 1);
        LocalDate hasta = LocalDate.of(2025, 1, 31);

        String texto = generador.obtenerReporte(TipoReporte.VENTAS).generarTexto(desde, hasta, AHORA);

        assertTrue(texto.contains("Sin registros en el periodo consultado."), texto);
        assertTrue(texto.contains("Registros encontrados: 0"), texto);
        assertFalse(texto.contains("Total ingresos netos"), texto);
    }

    @Test
    void testReporteConPeriodoInvertidoLanzaReporteException() {
        ReporteExportable ventas = generador.obtenerReporte(TipoReporte.VENTAS);

        assertThrows(ReporteException.class, () -> ventas.generarTexto(HOY, HOY.minusDays(1), AHORA));
    }

    @Test
    void testGeneradorEntregaUnReporteParaCadaTipo() throws Exception {
        for (TipoReporte tipo : TipoReporte.values()) {
            ReporteExportable reporte = generador.obtenerReporte(tipo);
            assertNotNull(reporte, tipo.name());
            String texto = reporte.generarTexto(HOY.minusDays(30), HOY.plusDays(30), AHORA);
            assertTrue(texto.contains(reporte.getTitulo().toUpperCase()), tipo.name());
            assertTrue(texto.contains("Registros encontrados:"), tipo.name());
        }
    }

    @Test
    void testReporteProductosIncluyeCanjesSinFuncion() throws Exception {
        // PAGADAS: C2 crispetas medianas, C3 dos gaseosas, C5 chocolatina y el canje de crispetas medianas
        // de Carlos (sin función). C4 está reembolsada y no cuenta.
        String texto = generador.obtenerReporte(TipoReporte.PRODUCTOS_COMBOS).generarTexto(HOY, HOY, AHORA);

        assertTrue(texto.contains("Total productos: 5"), texto);
        assertTrue(texto.contains("Total combos: 1"), texto);
    }

    @Test
    void testReportePuntosCuentaRedencionesConYSinFuncion() throws Exception {
        // Canje de producto de Carlos (400) y canje de entrada de Ana (1.000).
        String texto = generador.obtenerReporte(TipoReporte.PUNTOS).generarTexto(HOY, HOY, AHORA);

        assertTrue(texto.contains("Puntos redimidos: 1400"), texto);
        assertTrue(texto.contains("ANULADA por reembolso"), texto);
    }

    @Test
    void testReporteEntradasPorFuncionOmiteCanjesSinFuncion() throws Exception {
        // Entradas PAGADAS: C1 (2), C2 (1), C3 (2), C5 (1) y el canje de entrada de Ana (1).
        String texto = generador.obtenerReporte(TipoReporte.ENTRADAS_POR_FUNCION).generarTexto(HOY, HOY, AHORA);

        assertTrue(texto.contains("Total entradas: 7"), texto);
        assertTrue(texto.contains("Registros encontrados: 5"), texto);
    }

    @Test
    void testExportadorGuardaArchivoConNombreSugerido(@TempDir Path carpeta) throws Exception {
        String texto = generador.obtenerReporte(TipoReporte.VENTAS).generarTexto(HOY, HOY, AHORA);

        Path archivo = new ExportadorReporte().exportar(texto, carpeta, TipoReporte.VENTAS, AHORA);

        assertEquals("reporte_ventas_2026-03-10_1000.txt", archivo.getFileName().toString());
        assertEquals(texto, Files.readString(archivo, StandardCharsets.UTF_8));
    }

    @Test
    void testExportadorLanzaReporteExceptionSiNoPuedeEscribir(@TempDir Path carpeta) throws Exception {
        Path noEsCarpeta = Files.writeString(carpeta.resolve("archivo.txt"), "ocupado");

        assertThrows(ReporteException.class,
                () -> new ExportadorReporte().exportar("texto", noEsCarpeta, TipoReporte.VENTAS, AHORA));
    }
}
