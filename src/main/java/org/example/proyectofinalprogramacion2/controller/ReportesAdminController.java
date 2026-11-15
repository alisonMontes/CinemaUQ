package org.example.proyectofinalprogramacion2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.example.proyectofinalprogramacion2.exception.ReporteException;
import org.example.proyectofinalprogramacion2.report.ExportadorReporte;
import org.example.proyectofinalprogramacion2.report.GeneradorReportes;
import org.example.proyectofinalprogramacion2.report.TipoReporte;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Pestaña Reportes: genera el texto de un reporte para un periodo y lo exporta a TXT.
 */
public class ReportesAdminController implements PestanaAdmin {

    private static final int DIAS_PERIODO_INICIAL = 30;

    @FXML
    private ComboBox<TipoReporte> cmbTipo;
    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private TextArea txtReporte;
    @FXML
    private Label lblMensaje;

    private final GeneradorReportes generador = new GeneradorReportes();
    private final ExportadorReporte exportador = new ExportadorReporte();
    private File ultimaCarpeta;

    @FXML
    private void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(TipoReporte.values()));
        cmbTipo.setValue(TipoReporte.VENTAS);
        LocalDate hoy = LocalDateTime.now().toLocalDate();
        dpDesde.setValue(hoy.minusDays(DIAS_PERIODO_INICIAL));
        dpHasta.setValue(hoy);
    }

    @Override
    public void inicializar(AdminController principal) {
        // Los reportes no necesitan datos de la ventana principal.
    }

    @Override
    public void refrescar() {
        // El texto se genera solo cuando el administrador lo pide.
    }

    /**
     * Genera el texto del reporte elegido; si hay un error lo muestra y devuelve null.
     */
    private String generarTexto(LocalDateTime ahora) {
        try {
            TipoReporte tipo = Campos.seleccion(cmbTipo, "el tipo de reporte");
            LocalDate desde = Campos.fecha(dpDesde, "fecha inicial");
            LocalDate hasta = Campos.fecha(dpHasta, "fecha final");
            String texto = generador.obtenerReporte(tipo).generarTexto(desde, hasta, ahora);
            txtReporte.setText(texto);
            return texto;
        } catch (ReporteException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
            return null;
        }
    }

    @FXML
    private void generar() {
        if (generarTexto(LocalDateTime.now()) != null) {
            lblMensaje.setText("Reporte generado: " + cmbTipo.getValue().getDescripcion());
        }
    }

    @FXML
    private void exportar() {
        LocalDateTime ahora = LocalDateTime.now();
        String texto = generarTexto(ahora);
        if (texto == null) {
            return;
        }
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar reporte TXT");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de texto (*.txt)", "*.txt"));
        selector.setInitialFileName(ExportadorReporte.nombreSugerido(cmbTipo.getValue(), ahora));
        if (ultimaCarpeta != null && ultimaCarpeta.isDirectory()) {
            selector.setInitialDirectory(ultimaCarpeta);
        }
        File archivo = selector.showSaveDialog(NavegadorVistas.ventanaDe(txtReporte));
        if (archivo == null) {
            return;
        }
        try {
            Path guardado = exportador.guardar(texto, archivo.toPath());
            ultimaCarpeta = archivo.getParentFile();
            lblMensaje.setText("Exportado en " + guardado);
            Dialogos.info("Reporte exportado", "El reporte se guardó en:\n" + guardado.toAbsolutePath());
        } catch (ReporteException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
