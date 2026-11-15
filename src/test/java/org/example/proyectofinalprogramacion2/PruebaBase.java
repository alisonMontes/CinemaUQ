package org.example.proyectofinalprogramacion2;

import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Usuario;
import org.example.proyectofinalprogramacion2.service.DatosIniciales;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base de las pruebas: antes de cada prueba reinicia CinemaUQ y carga los datos iniciales
 * con una hora fija, para que los resultados no dependan del reloj.
 *
 * Con AHORA = 10/03/2026 10:00 las funciones quedan así:
 * F001 hoy 11:00, F002 hoy 20:00, F003 11/03 19:00, F004 12/03 20:00 (Sala 1),
 * F005 13/03 17:00, F006 15/03 15:00 (Sala 1), F007 16/03 21:00.
 */
public abstract class PruebaBase {

    protected static final LocalDateTime AHORA = LocalDateTime.of(2026, 3, 10, 10, 0);
    protected static final LocalDate HOY = AHORA.toLocalDate();
    protected static final String ANA = "ana@correo.com";
    protected static final String CARLOS = "carlos@correo.com";
    protected static final String LAURA = "laura@correo.com";

    protected CinemaUQ cinema;

    @BeforeEach
    void cargarDatosIniciales() {
        CinemaUQ.getInstancia().reiniciar();
        DatosIniciales.cargar(AHORA);
        cinema = CinemaUQ.getInstancia();
    }

    protected Cliente cliente(String correo) {
        return (Cliente) cinema.buscarUsuarioPorCorreo(correo);
    }

    protected Usuario administrador() {
        return cinema.buscarUsuarioPorCorreo(DatosIniciales.CORREO_ADMIN);
    }

    protected Funcion funcion(String codigo) {
        return cinema.buscarFuncion(codigo);
    }

    protected Asiento asiento(Funcion funcion, String fila, int numero) {
        return funcion.getSala().buscarAsiento(fila, numero);
    }

    protected Producto producto(String codigo) {
        return cinema.buscarProducto(codigo);
    }
}
