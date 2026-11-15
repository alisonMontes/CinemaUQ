package org.example.proyectofinalprogramacion2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.StringConverter;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.exception.SaldoInsuficienteException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Promocion;
import org.example.proyectofinalprogramacion2.service.CarteleraService;
import org.example.proyectofinalprogramacion2.service.CompraService;
import org.example.proyectofinalprogramacion2.service.TarjetaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña Resumen y pago: detalle del carrito, promoción vigente opcional, total y pago con la tarjeta.
 */
public class ResumenPagoController implements PestanaCliente {

    private static final String MENSAJE_SALDO_INSUFICIENTE = "Saldo insuficiente: solicita una recarga al administrador";

    @FXML
    private Label lblFuncion;
    @FXML
    private ListView<String> listaDetalle;
    @FXML
    private ComboBox<Promocion> cmbPromocion;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblSaldo;
    @FXML
    private Label lblMensaje;
    @FXML
    private Button btnPagar;

    private final CarteleraService carteleraService = new CarteleraService();
    private final CompraService compraService = new CompraService();
    private final TarjetaService tarjetaService = new TarjetaService();
    private ClienteController principal;

    @FXML
    private void initialize() {
        cmbPromocion.setConverter(new StringConverter<>() {
            @Override
            public String toString(Promocion promocion) {
                return promocion == null ? "" : promocion.getNombre() + " (-"
                        + Formato.porcentaje(promocion.getPorcentajeDescuento())
                        + ", hasta " + Formato.fecha(promocion.getFechaFin()) + ")";
            }

            @Override
            public Promocion fromString(String texto) {
                return null;
            }
        });
        cmbPromocion.valueProperty().addListener((obs, anterior, nueva) -> calcularTotales());
    }

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        lblMensaje.setText("");
        Promocion elegida = cmbPromocion.getValue();
        List<Promocion> vigentes = carteleraService.getPromocionesVigentes(LocalDateTime.now().toLocalDate());
        cmbPromocion.setItems(FXCollections.observableArrayList(vigentes));
        cmbPromocion.setValue(vigentes.contains(elegida) ? elegida : null);
        mostrarDetalle();
        calcularTotales();
        mostrarSaldo();
    }

    private void mostrarDetalle() {
        CarritoCompra carrito = principal.getCarrito();
        Funcion funcion = carrito.getFuncion();
        List<String> lineas = new ArrayList<>();
        if (funcion == null) {
            lblFuncion.setText("Sin función elegida");
        } else {
            lblFuncion.setText(funcion.getPelicula().getTitulo() + "  ·  " + funcion.getSala().getNombre()
                    + "  ·  " + Formato.fechaHora(funcion.getFechaHora()));
            for (Asiento asiento : carrito.getAsientos()) {
                lineas.add("Entrada " + asiento + "   " + Formato.dinero(funcion.getPrecioBase()));
            }
        }
        for (Producto producto : carrito.getProductos()) {
            lineas.add(producto.getNombre() + "   " + Formato.dinero(producto.getPrecio()));
        }
        for (Combo combo : carrito.getCombos()) {
            lineas.add(combo.getNombre() + "   " + Formato.dinero(combo.getPrecio()));
        }
        listaDetalle.setItems(FXCollections.observableArrayList(lineas));
    }

    private void calcularTotales() {
        if (principal == null) {
            return;
        }
        CarritoCompra carrito = principal.getCarrito();
        if (carrito.getFuncion() == null || carrito.getAsientos().isEmpty()) {
            lblSubtotal.setText("Subtotal: -");
            lblDescuento.setText("Descuento: -");
            lblTotal.setText("Total: -");
            lblMensaje.setText("Para pagar elige una función y al menos un asiento");
            btnPagar.setDisable(true);
            return;
        }
        try {
            LocalDateTime ahora = LocalDateTime.now();
            double subtotal = compraService.calcularTotal(principal.getCliente(), carrito.getFuncion(),
                    carrito.getAsientos(), carrito.getProductos(), carrito.getCombos(), null, ahora);
            double total = compraService.calcularTotal(principal.getCliente(), carrito.getFuncion(),
                    carrito.getAsientos(), carrito.getProductos(), carrito.getCombos(), cmbPromocion.getValue(), ahora);
            lblSubtotal.setText("Subtotal: " + Formato.dinero(subtotal));
            lblDescuento.setText("Descuento: " + Formato.dinero(subtotal - total));
            lblTotal.setText("Total: " + Formato.dinero(total));
            btnPagar.setDisable(false);
        } catch (CinemaUQException e) {
            lblTotal.setText("Total: -");
            lblMensaje.setText(e.getMessage());
            btnPagar.setDisable(true);
        }
    }

    private void mostrarSaldo() {
        Cliente cliente = principal.getCliente();
        lblSaldo.setText("Saldo de tu tarjeta: " + Formato.dinero(tarjetaService.consultarSaldo(cliente))
                + " (" + cliente.getTarjeta().getEstado() + ")");
    }

    @FXML
    private void quitarPromocion() {
        cmbPromocion.setValue(null);
    }

    @FXML
    private void pagar() {
        CarritoCompra carrito = principal.getCarrito();
        try {
            Compra compra = compraService.comprarEntradas(principal.getCliente(), carrito.getFuncion(),
                    carrito.getAsientos(), carrito.getProductos(), carrito.getCombos(), cmbPromocion.getValue(),
                    LocalDateTime.now());
            carrito.vaciar();
            cmbPromocion.setValue(null);
            Dialogos.info("Compra realizada", "Compra " + compra.getCodigo() + " pagada por "
                    + Formato.dinero(compra.getTotal()) + ".\nNuevo saldo: "
                    + Formato.dinero(tarjetaService.consultarSaldo(principal.getCliente())) + ".");
            refrescar();
        } catch (SaldoInsuficienteException e) {
            Dialogos.error(MENSAJE_SALDO_INSUFICIENTE);
            lblMensaje.setText(MENSAJE_SALDO_INSUFICIENTE);
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
            refrescar();
        }
    }
}
