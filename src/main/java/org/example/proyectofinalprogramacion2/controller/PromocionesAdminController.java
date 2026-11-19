package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Promocion;
import org.example.proyectofinalprogramacion2.service.AdministracionService;

import java.time.LocalDateTime;

/**
 * Pestaña Promociones: crear promociones con porcentaje y vigencia.
 */
public class PromocionesAdminController implements PestanaAdmin {

    @FXML
    private TableView<Promocion> tablaPromociones;
    @FXML
    private TableColumn<Promocion, String> colCodigo;
    @FXML
    private TableColumn<Promocion, String> colNombre;
    @FXML
    private TableColumn<Promocion, String> colPorcentaje;
    @FXML
    private TableColumn<Promocion, String> colInicio;
    @FXML
    private TableColumn<Promocion, String> colFin;
    @FXML
    private TableColumn<Promocion, String> colVigente;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPorcentaje;
    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFin;
    @FXML
    private Label lblMensaje;

    private final AdministracionService administracionService = new AdministracionService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPorcentaje.setCellValueFactory(c -> new SimpleStringProperty(
                Formato.porcentaje(c.getValue().getPorcentajeDescuento())));
        colInicio.setCellValueFactory(c -> new SimpleStringProperty(Formato.fecha(c.getValue().getFechaInicio())));
        colFin.setCellValueFactory(c -> new SimpleStringProperty(Formato.fecha(c.getValue().getFechaFin())));
        colVigente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().estaVigente(LocalDateTime.now().toLocalDate()) ? "Sí" : "No"));
    }

    @Override
    public void inicializar(AdminController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        try {
            tablaPromociones.setItems(FXCollections.observableArrayList(
                    administracionService.listarPromociones(principal.getAdministrador())));
            tablaPromociones.refresh();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** Botón para crear la promoción con su porcentaje y vigencia. */
    @FXML
    private void crear() {
        try {
            Promocion promocion = new Promocion(Campos.texto(txtCodigo), Campos.texto(txtNombre),
                    Campos.decimal(txtPorcentaje, "porcentaje de descuento"),
                    Campos.fecha(dpInicio, "fecha de inicio"), Campos.fecha(dpFin, "fecha de fin"));
            administracionService.crearPromocion(principal.getAdministrador(), promocion);
            txtCodigo.clear();
            txtNombre.clear();
            txtPorcentaje.clear();
            dpInicio.setValue(null);
            dpFin.setValue(null);
            refrescar();
            lblMensaje.setText("Promoción " + promocion.getNombre() + " creada");
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
