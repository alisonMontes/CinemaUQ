package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.AcumulacionPuntos;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.ConfiguracionPuntos;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.service.CarteleraService;
import org.example.proyectofinalprogramacion2.service.CompraService;
import org.example.proyectofinalprogramacion2.service.PuntosService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pestaña Puntos: puntos vigentes y vencidos, acumulaciones y redención de beneficios.
 */
public class PuntosController implements PestanaCliente {

    @FXML
    private Label lblVigentes;
    @FXML
    private Label lblVencidos;
    @FXML
    private TableView<AcumulacionPuntos> tablaAcumulaciones;
    @FXML
    private TableColumn<AcumulacionPuntos, String> colObtencion;
    @FXML
    private TableColumn<AcumulacionPuntos, String> colIniciales;
    @FXML
    private TableColumn<AcumulacionPuntos, String> colDisponibles;
    @FXML
    private TableColumn<AcumulacionPuntos, String> colVencimiento;
    @FXML
    private TableColumn<AcumulacionPuntos, String> colEstado;
    @FXML
    private Label lblCostoEntrada;
    @FXML
    private Label lblEntrada;
    @FXML
    private Label lblCostoProducto;
    @FXML
    private ComboBox<Producto> cmbProducto;
    @FXML
    private Label lblCostoCombo;
    @FXML
    private ComboBox<Combo> cmbCombo;

    private final PuntosService puntosService = new PuntosService();
    private final CompraService compraService = new CompraService();
    private final CarteleraService carteleraService = new CarteleraService();
    private ClienteController principal;

    @FXML
    private void initialize() {
        colObtencion.setCellValueFactory(c -> new SimpleStringProperty(Formato.fecha(c.getValue().getFechaObtencion())));
        colIniciales.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntosIniciales())));
        colDisponibles.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getPuntosDisponibles())));
        colVencimiento.setCellValueFactory(c -> new SimpleStringProperty(
                Formato.fecha(c.getValue().getFechaVencimiento())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(estado(c.getValue())));

        lblCostoEntrada.setText("Entrada: " + ConfiguracionPuntos.PUNTOS_ENTRADA + " puntos");
        lblCostoProducto.setText("Producto: " + ConfiguracionPuntos.PUNTOS_PRODUCTO + " puntos");
        lblCostoCombo.setText("Combo: " + ConfiguracionPuntos.PUNTOS_COMBO + " puntos");
        cmbProducto.setConverter(convertidorPorNombre());
        cmbCombo.setConverter(convertidorPorNombre());
    }

    private <T extends ItemVendible> StringConverter<T> convertidorPorNombre() {
        return new StringConverter<>() {
            @Override
            public String toString(T item) {
                return item == null ? "" : item.getNombre();
            }

            @Override
            public T fromString(String texto) {
                return null;
            }
        };
    }

    private String estado(AcumulacionPuntos acumulacion) {
        if (acumulacion.isAnulada()) {
            return "Anulada";
        }
        if (acumulacion.estaVencida(LocalDate.now())) {
            return "Vencida";
        }
        return acumulacion.getPuntosDisponibles() == 0 ? "Usada" : "Vigente";
    }

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        Cliente cliente = principal.getCliente();
        LocalDate hoy = LocalDateTime.now().toLocalDate();
        lblVigentes.setText(String.valueOf(puntosService.getPuntosVigentes(cliente, hoy)));
        lblVencidos.setText(String.valueOf(puntosService.getPuntosVencidos(cliente, hoy)));

        List<AcumulacionPuntos> acumulaciones = new ArrayList<>(cliente.getCuentaPuntos().getAcumulaciones());
        acumulaciones.sort(Comparator.comparing(AcumulacionPuntos::getFechaVencimiento));
        tablaAcumulaciones.setItems(FXCollections.observableArrayList(acumulaciones));
        tablaAcumulaciones.refresh();

        cmbProducto.setItems(FXCollections.observableArrayList(carteleraService.getProductos()));
        cmbCombo.setItems(FXCollections.observableArrayList(carteleraService.getCombos()));
        mostrarEntradaElegida();
    }

    private void mostrarEntradaElegida() {
        CarritoCompra carrito = principal.getCarrito();
        if (carrito.getFuncion() == null || carrito.getAsientos().size() != 1) {
            lblEntrada.setText("Elige una función y un solo asiento en las pestañas Cartelera y Asientos.");
        } else {
            Funcion funcion = carrito.getFuncion();
            lblEntrada.setText(funcion.getPelicula().getTitulo() + " - " + Formato.fechaHora(funcion.getFechaHora())
                    + " - asiento " + carrito.getAsientos().get(0));
        }
    }

    @FXML
    private void canjearEntrada() {
        CarritoCompra carrito = principal.getCarrito();
        if (carrito.getFuncion() == null || carrito.getAsientos().size() != 1) {
            Dialogos.error("Para canjear una entrada elige una función y exactamente un asiento.");
            return;
        }
        Asiento asiento = carrito.getAsientos().get(0);
        if (!confirmarCanje("una entrada (asiento " + asiento.getCodigo() + ")", ConfiguracionPuntos.PUNTOS_ENTRADA)) {
            return;
        }
        try {
            Compra compra = compraService.redimirEntrada(principal.getCliente(), carrito.getFuncion(), asiento,
                    LocalDateTime.now());
            carrito.quitarAsiento(asiento);
            avisarCanje(compra);
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
        refrescar();
    }

    @FXML
    private void canjearProducto() {
        Producto producto = cmbProducto.getValue();
        if (producto == null) {
            Dialogos.error("Selecciona el producto que deseas canjear.");
            return;
        }
        if (!confirmarCanje(producto.getNombre(), ConfiguracionPuntos.PUNTOS_PRODUCTO)) {
            return;
        }
        try {
            avisarCanje(compraService.redimirProducto(principal.getCliente(), producto, LocalDateTime.now()));
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
        refrescar();
    }

    @FXML
    private void canjearCombo() {
        Combo combo = cmbCombo.getValue();
        if (combo == null) {
            Dialogos.error("Selecciona el combo que deseas canjear.");
            return;
        }
        if (!confirmarCanje(combo.getNombre(), ConfiguracionPuntos.PUNTOS_COMBO)) {
            return;
        }
        try {
            avisarCanje(compraService.redimirCombo(principal.getCliente(), combo, LocalDateTime.now()));
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
        refrescar();
    }

    private boolean confirmarCanje(String beneficio, int puntos) {
        return Dialogos.confirmar("Canjear puntos", "¿Deseas canjear " + beneficio + " por " + puntos + " puntos?");
    }

    private void avisarCanje(Compra compra) {
        Dialogos.info("Canje realizado", "Canje " + compra.getCodigo() + " registrado. Se usaron "
                + compra.getPuntosUsados() + " puntos.");
    }
}
