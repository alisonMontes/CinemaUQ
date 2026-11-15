package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.AcumulacionPuntos;
import org.example.proyectofinalprogramacion2.model.Administrador;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Promocion;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.model.TipoAsiento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

/**
 * Carga los datos de demostración en la instancia única de CinemaUQ.
 * Las fechas se calculan desde "ahora". Clientes, recargas, catálogo, compras, cancelaciones
 * y canjes pasan por los servicios; solo el administrador y los puntos históricos se agregan directo.
 */
// SRP: solo carga los datos de demostración.
public final class DatosIniciales {

    public static final String CORREO_ADMIN = "admin@cinemauq.com";
    public static final String CLAVE_ADMIN = "admin123";

    private DatosIniciales() {
    }

    /**
     * Reinicia CinemaUQ y carga los datos de demostración.
     */
    public static void cargar(LocalDateTime ahora) {
        try {
            new Cargador(ahora.truncatedTo(ChronoUnit.MINUTES)).cargar();
        } catch (CinemaUQException e) {
            throw new IllegalStateException("No se pudieron cargar los datos iniciales: " + e.getMessage(), e);
        }
    }

    private static final class Cargador {

        private final LocalDateTime ahora;
        private final LocalDate hoy;
        private final CinemaUQ cinema = CinemaUQ.getInstancia();
        private final AutenticacionService autenticacion = new AutenticacionService();
        private final TarjetaService tarjetas = new TarjetaService();
        private final AdministracionService administracion = new AdministracionService();
        private final CompraService compras = new CompraService();
        private final CancelacionService cancelaciones = new CancelacionService();
        private Administrador admin;

        Cargador(LocalDateTime ahora) {
            this.ahora = ahora;
            this.hoy = ahora.toLocalDate();
        }

        void cargar() throws CinemaUQException {
            cinema.reiniciar();
            admin = new Administrador("Administrador CinemaUQ", CORREO_ADMIN, CLAVE_ADMIN);
            cinema.agregarUsuario(admin);

            // ---------- Clientes y tarjetas ----------
            Cliente ana = autenticacion.registrarCliente("Ana María Gómez", "ana@correo.com", "ana123");
            Cliente carlos = autenticacion.registrarCliente("Carlos Andrés Ruiz", "carlos@correo.com", "carlos123");
            Cliente laura = autenticacion.registrarCliente("Laura Sofía Pérez", "laura@correo.com", "laura123");
            tarjetas.recargar(admin, ana, 200_000, ahora.minusDays(3));
            tarjetas.recargar(admin, carlos, 150_000, ahora.minusDays(2));
            tarjetas.recargar(admin, laura, 60_000, ahora.minusDays(1));

            // ---------- Películas ----------
            Pelicula dune = new Pelicula("P001", "Dune: Parte Dos", "Ciencia ficción", 166, "+12",
                    "Paul Atreides se une a los Fremen para vengar a su familia.");
            Pelicula intensamente = new Pelicula("P002", "Intensamente 2", "Animación", 100, "Todo público",
                    "Nuevas emociones llegan a la mente de Riley en su adolescencia.");
            Pelicula oppenheimer = new Pelicula("P003", "Oppenheimer", "Drama histórico", 180, "+15",
                    "La historia del físico que dirigió el Proyecto Manhattan.");
            Pelicula deadpool = new Pelicula("P004", "Deadpool & Wolverine", "Acción", 128, "+18",
                    "Deadpool recluta a Wolverine para salvar su universo.");
            for (Pelicula pelicula : List.of(dune, intensamente, oppenheimer, deadpool)) {
                administracion.crearPelicula(admin, pelicula);
            }

            // ---------- Salas y asientos ----------
            Sala sala1 = crearSala("S01", "Sala 1", 6, 10, Set.of("A1", "A2"));
            Sala sala2 = crearSala("S02", "Sala 2", 8, 12, Set.of("A1", "A2", "A11", "A12"));
            Sala salaVip = crearSala("S03", "Sala VIP", 4, 6, Set.of("A1"));

            // ---------- Funciones (fechas relativas a ahora) ----------
            LocalDateTime enUnaHora = ahora.plusHours(1);
            LocalDateTime enDiezHoras = ahora.plusHours(10);
            Funcion f1 = crearFuncion("F001", dune, sala1, enUnaHora.toLocalDate(), enUnaHora.toLocalTime(), 18_000);
            Funcion f2 = crearFuncion("F002", intensamente, sala2, enDiezHoras.toLocalDate(),
                    enDiezHoras.toLocalTime(), 16_000);
            Funcion f3 = crearFuncion("F003", oppenheimer, salaVip, hoy.plusDays(1), LocalTime.of(19, 0), 20_000);
            Funcion f4 = crearFuncion("F004", deadpool, sala1, hoy.plusDays(2), LocalTime.of(20, 0), 18_000);
            Funcion f5 = crearFuncion("F005", dune, sala2, hoy.plusDays(3), LocalTime.of(17, 0), 17_000);
            Funcion f6 = crearFuncion("F006", intensamente, sala1, hoy.plusDays(5), LocalTime.of(15, 0), 16_000);
            crearFuncion("F007", oppenheimer, salaVip, hoy.plusDays(6), LocalTime.of(21, 0), 20_000);

            // ---------- Productos ----------
            Producto crispetasGrandes = crearProducto("PR01", "Crispetas grandes", 12_000, "Crispetas de sal 170 g");
            Producto crispetasMedianas = crearProducto("PR02", "Crispetas medianas", 9_000, "Crispetas de sal 110 g");
            Producto gaseosa = crearProducto("PR03", "Gaseosa", 6_000, "Gaseosa 22 oz");
            crearProducto("PR04", "Agua", 4_000, "Agua sin gas 600 ml");
            Producto perro = crearProducto("PR05", "Perro caliente", 10_000, "Perro caliente clásico");
            Producto chocolatina = crearProducto("PR06", "Chocolatina", 5_000, "Chocolatina 40 g");

            // ---------- Combos (el familiar contiene otro combo: Composite) ----------
            Combo individual = new Combo("CB01", "Combo Individual", 10);
            individual.agregarComponente(crispetasMedianas);
            individual.agregarComponente(gaseosa);
            Combo pareja = new Combo("CB02", "Combo Pareja", 15);
            pareja.agregarComponente(crispetasGrandes);
            pareja.agregarComponente(gaseosa);
            pareja.agregarComponente(gaseosa);
            Combo familiar = new Combo("CB03", "Combo Familiar", 10);
            familiar.agregarComponente(pareja);
            familiar.agregarComponente(perro);
            familiar.agregarComponente(perro);
            familiar.agregarComponente(chocolatina);
            for (Combo combo : List.of(individual, pareja, familiar)) {
                administracion.crearCombo(admin, combo);
            }

            // ---------- Promociones ----------
            Promocion semanaCine = new Promocion("PM01", "Semana del cine", 20, hoy, hoy.plusDays(30));
            Promocion estudiantes = new Promocion("PM02", "Estudiantes UQ", 10, hoy.minusDays(10), hoy.plusDays(60));
            Promocion navidad = new Promocion("PM03", "Temporada navideña", 25, hoy.plusDays(30), hoy.plusDays(45));
            Promocion halloween = new Promocion("PM04", "Halloween", 15, hoy.minusDays(30), hoy.minusDays(15));
            for (Promocion promocion : List.of(semanaCine, estudiantes, navidad, halloween)) {
                administracion.crearPromocion(admin, promocion);
            }

            // ---------- Puntos históricos (distintos vencimientos, uno ya vencido) ----------
            agregarPuntos(ana, 600, hoy.minusMonths(11).minusDays(20));   // vence en unos 10 días
            agregarPuntos(ana, 900, hoy.minusMonths(6));
            agregarPuntos(ana, 500, hoy.minusMonths(13));                 // vencida
            agregarPuntos(carlos, 1_500, hoy.minusMonths(2));
            agregarPuntos(laura, 450, hoy.minusMonths(3));
            agregarPuntos(laura, 300, hoy.minusMonths(14));               // vencida

            // ---------- Compras con tarjeta ----------
            compras.comprarEntradas(ana, f1, List.of(asiento(sala1, "A", 3), asiento(sala1, "A", 4)),
                    List.of(), List.of(pareja), null, ahora);                       // empieza en 1 h: no cancelable
            compras.comprarEntradas(ana, f3, List.of(asiento(salaVip, "A", 1)),
                    List.of(crispetasMedianas), List.of(), estudiantes, ahora);     // entrada accesible
            compras.comprarEntradas(carlos, f2, List.of(asiento(sala2, "B", 5), asiento(sala2, "B", 6)),
                    List.of(gaseosa, gaseosa), List.of(), semanaCine, ahora);       // en 10 h: cancelable al 80 %
            Compra paraCancelar = compras.comprarEntradas(carlos, f5, List.of(asiento(sala2, "C", 3)),
                    List.of(), List.of(individual), null, ahora);
            compras.comprarEntradas(laura, f4, List.of(asiento(sala1, "D", 4)),
                    List.of(chocolatina), List.of(), null, ahora);

            // ---------- Cancelación de ejemplo (más de 24 h: 100 %) ----------
            cancelaciones.cancelarCompra(carlos, paraCancelar, ahora);

            // ---------- Canjes con puntos ----------
            compras.redimirProducto(carlos, crispetasMedianas, ahora);                    // compra sin función
            compras.redimirEntrada(ana, f6, asiento(sala1, "B", 2), ahora);               // usa primero los 600 por vencer

            // ---------- Tarjeta bloqueada ----------
            tarjetas.bloquear(admin, laura);
        }

        private Sala crearSala(String codigo, String nombre, int filas, int asientosPorFila, Set<String> accesibles)
                throws CinemaUQException {
            Sala sala = new Sala(codigo, nombre);
            for (int f = 0; f < filas; f++) {
                String fila = String.valueOf((char) ('A' + f));
                for (int n = 1; n <= asientosPorFila; n++) {
                    TipoAsiento tipo = accesibles.contains(fila + n) ? TipoAsiento.ACCESIBLE : TipoAsiento.NORMAL;
                    sala.agregarAsiento(new Asiento(fila, n, tipo));
                }
            }
            administracion.crearSala(admin, sala);
            return sala;
        }

        private Funcion crearFuncion(String codigo, Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime hora,
                                     double precio) throws CinemaUQException {
            Funcion funcion = new Funcion(codigo, pelicula, sala, fecha, hora, precio);
            administracion.crearFuncion(admin, funcion);
            return funcion;
        }

        private Producto crearProducto(String codigo, String nombre, double precio, String descripcion)
                throws CinemaUQException {
            Producto producto = new Producto(codigo, nombre, precio, descripcion);
            administracion.crearProducto(admin, producto);
            return producto;
        }

        private void agregarPuntos(Cliente cliente, int puntos, LocalDate fechaObtencion) {
            cliente.getCuentaPuntos().agregarAcumulacion(new AcumulacionPuntos(puntos, fechaObtencion, null));
        }

        private Asiento asiento(Sala sala, String fila, int numero) {
            return sala.buscarAsiento(fila, numero);
        }
    }
}
