package org.example.proyectofinalprogramacion2;

import org.example.proyectofinalprogramacion2.decorator.PromocionDecorator;
import org.example.proyectofinalprogramacion2.exception.PromocionNoVigenteException;
import org.example.proyectofinalprogramacion2.exception.TipoAsientoInvalidoException;
import org.example.proyectofinalprogramacion2.factory.EntradaAccesibleFactory;
import org.example.proyectofinalprogramacion2.factory.EntradaFactory;
import org.example.proyectofinalprogramacion2.factory.EntradaGeneralFactory;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.EntradaAccesible;
import org.example.proyectofinalprogramacion2.model.EntradaGeneral;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Promocion;
import org.example.proyectofinalprogramacion2.service.CompraService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Composite (combos), Decorator (promociones) y Factory Method (entradas).
 */
class PatronesTest extends PruebaBase {

    private final Producto crispetas = new Producto("T01", "Crispetas", 10_000, "");
    private final Producto gaseosa = new Producto("T02", "Gaseosa", 6_000, "");

    // ---------- Composite ----------

    @Test
    void testComboAplicaDescuentoSobreSusComponentes() {
        Combo combo = new Combo("TC1", "Combo prueba", 10);
        combo.agregarComponente(crispetas);
        combo.agregarComponente(gaseosa);

        assertEquals(16_000, combo.getPrecioRegular(), 0.001);
        assertEquals(14_400, combo.getPrecio(), 0.001);
        assertEquals(1_600, combo.getAhorro(), 0.001);
    }

    @Test
    void testComboDentroDeOtroComboCalculaPrecioRecursivo() {
        Combo interno = new Combo("TC1", "Interno", 10);
        interno.agregarComponente(crispetas);
        interno.agregarComponente(gaseosa);                         // $14.400
        Combo externo = new Combo("TC2", "Externo", 20);
        externo.agregarComponente(interno);
        externo.agregarComponente(new Producto("T03", "Chocolatina", 5_000, ""));

        assertEquals(19_400, externo.getPrecioRegular(), 0.001);
        assertEquals(15_520, externo.getPrecio(), 0.001);
    }

    @Test
    void testComboNoPuedeContenerseASiMismo() {
        Combo combo = new Combo("TC1", "Combo", 10);
        assertThrows(IllegalArgumentException.class, () -> combo.agregarComponente(combo));
    }

    // ---------- Decorator ----------

    @Test
    void testDecoratorAplicaPromocionVigente() {
        Promocion vigente = new Promocion("TP1", "Vigente", 20, HOY.minusDays(1), HOY.plusDays(1));
        PromocionDecorator conPromocion = new PromocionDecorator(crispetas, vigente, HOY);

        assertTrue(conPromocion.aplicaDescuento());
        assertEquals(8_000, conPromocion.getPrecio(), 0.001);
        assertEquals(10_000, crispetas.getPrecio(), 0.001, "El ítem original no cambia");
    }

    @Test
    void testDecoratorNoAplicaPromocionNoVigente() {
        Promocion vencida = new Promocion("TP1", "Vencida", 20, HOY.minusDays(10), HOY.minusDays(1));
        Promocion futura = new Promocion("TP2", "Futura", 20, HOY.plusDays(1), HOY.plusDays(10));

        assertEquals(10_000, new PromocionDecorator(crispetas, vencida, HOY).getPrecio(), 0.001);
        assertEquals(10_000, new PromocionDecorator(crispetas, futura, HOY).getPrecio(), 0.001);
    }

    @Test
    void testDecoratorRespetaLosDiasLimiteDeLaPromocion() {
        Promocion promocion = new Promocion("TP1", "Límites", 20, HOY, HOY.plusDays(5));

        assertEquals(8_000, new PromocionDecorator(crispetas, promocion, HOY).getPrecio(), 0.001);
        assertEquals(8_000, new PromocionDecorator(crispetas, promocion, HOY.plusDays(5)).getPrecio(), 0.001);
        assertEquals(10_000, new PromocionDecorator(crispetas, promocion, HOY.plusDays(6)).getPrecio(), 0.001);
        assertEquals(10_000, new PromocionDecorator(crispetas, promocion, HOY.minusDays(1)).getPrecio(), 0.001);
    }

    @Test
    void testCompraConPromocionNoVigenteLanzaExcepcion() {
        Promocion navidad = cinema.buscarPromocion("PM03");          // empieza en 30 días
        Funcion funcion = funcion("F004");

        assertThrows(PromocionNoVigenteException.class, () -> new CompraService().comprarEntradas(cliente(ANA),
                funcion, List.of(asiento(funcion, "C", 5)), List.of(), List.of(), navidad, AHORA));
        assertTrue(funcion.estaDisponible(asiento(funcion, "C", 5)));
    }

    // ---------- Factory Method ----------

    @Test
    void testFabricaGeneralRechazaAsientoAccesible() {
        Funcion funcion = funcion("F001");
        Asiento accesible = asiento(funcion, "A", 1);
        assertTrue(accesible.esAccesible());

        assertThrows(TipoAsientoInvalidoException.class,
                () -> new EntradaGeneralFactory().crearEntrada(funcion, accesible));
    }

    @Test
    void testFabricaAccesibleRechazaAsientoNormal() {
        Funcion funcion = funcion("F001");
        Asiento normal = asiento(funcion, "B", 5);
        assertFalse(normal.esAccesible());

        assertThrows(TipoAsientoInvalidoException.class,
                () -> new EntradaAccesibleFactory().crearEntrada(funcion, normal));
    }

    @Test
    void testSegunAsientoEligeLaFabricaYLaEntradaCorrectas() throws Exception {
        Funcion funcion = funcion("F001");
        Asiento accesible = asiento(funcion, "A", 1);
        Asiento normal = asiento(funcion, "B", 5);

        assertInstanceOf(EntradaAccesibleFactory.class, EntradaFactory.segunAsiento(accesible));
        assertInstanceOf(EntradaGeneralFactory.class, EntradaFactory.segunAsiento(normal));
        assertInstanceOf(EntradaAccesible.class, EntradaFactory.segunAsiento(accesible).crearEntrada(funcion, accesible));
        assertInstanceOf(EntradaGeneral.class, EntradaFactory.segunAsiento(normal).crearEntrada(funcion, normal));
    }
}
