package org.example.proyectofinalprogramacion2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.service.CarteleraService;
import org.example.proyectofinalprogramacion2.service.CompraService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Pestaña Asientos: mapa de la sala para la función elegida, con un botón por asiento.
 */
public class AsientosController implements PestanaCliente {

    @FXML
    private Label lblFuncion;
    @FXML
    private GridPane gridAsientos;
    @FXML
    private Label lblSeleccion;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblMensaje;

    private final CarteleraService carteleraService = new CarteleraService();
    private final CompraService compraService = new CompraService();
    private ClienteController principal;

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        lblMensaje.setText("");
        dibujarAsientos();
        actualizarSeleccion();
    }

    private void dibujarAsientos() {
        gridAsientos.getChildren().clear();
        Funcion funcion = principal.getCarrito().getFuncion();
        if (funcion == null) {
            lblFuncion.setText("Elige una función en la pestaña Cartelera");
            return;
        }
        lblFuncion.setText(funcion.getPelicula().getTitulo() + "  ·  " + funcion.getSala().getNombre()
                + "  ·  " + Formato.fechaHora(funcion.getFechaHora())
                + "  ·  Entrada " + Formato.dinero(funcion.getPrecioBase()));

        List<Asiento> disponibles = carteleraService.getAsientosDisponibles(funcion);
        principal.getCarrito().conservarSolo(disponibles);

        // Filas (A, B, C...) en orden; la columna es el número del asiento.
        List<String> filas = new ArrayList<>();
        int maxNumero = 0;
        for (Asiento asiento : funcion.getSala().getAsientos()) {
            if (!filas.contains(asiento.getFila())) {
                filas.add(asiento.getFila());
            }
            maxNumero = Math.max(maxNumero, asiento.getNumero());
        }
        filas.sort(String::compareTo);

        for (int numero = 1; numero <= maxNumero; numero++) {
            gridAsientos.add(encabezado(String.valueOf(numero)), numero, 0);
        }
        for (int i = 0; i < filas.size(); i++) {
            gridAsientos.add(encabezado(filas.get(i)), 0, i + 1);
        }
        for (Asiento asiento : funcion.getSala().getAsientos()) {
            gridAsientos.add(crearBoton(asiento, disponibles.contains(asiento)),
                    asiento.getNumero(), filas.indexOf(asiento.getFila()) + 1);
        }
    }

    private Label encabezado(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("encabezado-asientos");
        return label;
    }

    private Button crearBoton(Asiento asiento, boolean disponible) {
        Button boton = new Button(asiento.getCodigo());
        boton.getStyleClass().add("asiento");
        if (!disponible) {
            boton.getStyleClass().add("asiento-ocupado");
            boton.setDisable(true);
        } else if (principal.getCarrito().contieneAsiento(asiento)) {
            boton.getStyleClass().add("asiento-seleccionado");
        } else if (asiento.esAccesible()) {
            boton.getStyleClass().add("asiento-accesible");
        } else {
            boton.getStyleClass().add("asiento-disponible");
        }
        boton.setOnAction(e -> {
            principal.getCarrito().alternarAsiento(asiento);
            refrescar();
        });
        return boton;
    }

    private void actualizarSeleccion() {
        CarritoCompra carrito = principal.getCarrito();
        if (carrito.getAsientos().isEmpty()) {
            lblSeleccion.setText("Ningún asiento seleccionado");
            lblSubtotal.setText("Subtotal: " + Formato.dinero(0));
            return;
        }
        StringJoiner codigos = new StringJoiner(", ");
        for (Asiento asiento : carrito.getAsientos()) {
            codigos.add(asiento.toString());
        }
        lblSeleccion.setText("Seleccionados (" + carrito.getAsientos().size() + "): " + codigos);
        try {
            double subtotal = compraService.calcularTotal(principal.getCliente(), carrito.getFuncion(),
                    carrito.getAsientos(), null, null, null, LocalDateTime.now());
            lblSubtotal.setText("Subtotal: " + Formato.dinero(subtotal));
        } catch (CinemaUQException e) {
            lblSubtotal.setText("Subtotal: -");
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Botón para quitar del carrito todos los asientos elegidos. */
    @FXML
    private void limpiarSeleccion() {
        for (Asiento asiento : new ArrayList<>(principal.getCarrito().getAsientos())) {
            principal.getCarrito().quitarAsiento(asiento);
        }
        refrescar();
    }

    /** Botón para pasar a confitería si hay al menos un asiento elegido. */
    @FXML
    private void continuar() {
        if (principal.getCarrito().getAsientos().isEmpty()) {
            lblMensaje.setText("Selecciona al menos un asiento");
            return;
        }
        principal.irAConfiteria();
    }
}
