package org.example.proyectofinalprogramacion2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.example.proyectofinalprogramacion2.model.Usuario;

import java.util.HashMap;
import java.util.Map;

/**
 * Ventana principal del administrador. Contiene las pestañas (cada una con su controlador)
 * y guarda el administrador autenticado, que las pestañas entregan a los services.
 */
public class AdminController {

    /** Archivo FXML de esta ventana (en la carpeta view). */
    public static final String VISTA = "admin-view.fxml";

    @FXML
    private Label lblBienvenida;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabPeliculas;
    @FXML
    private Tab tabSalas;
    @FXML
    private Tab tabFunciones;
    @FXML
    private Tab tabCatalogo;
    @FXML
    private Tab tabPromociones;
    @FXML
    private Tab tabClientes;
    @FXML
    private Tab tabTarjetas;
    @FXML
    private Tab tabCancelarFuncion;
    @FXML
    private Tab tabEstadisticas;

    // Controladores de las pestañas incluidas con fx:include (fx:id + "Controller").
    @FXML
    private PeliculasAdminController peliculasController;
    @FXML
    private SalasAdminController salasController;
    @FXML
    private FuncionesAdminController funcionesController;
    @FXML
    private CatalogoAdminController catalogoController;
    @FXML
    private PromocionesAdminController promocionesController;
    @FXML
    private ClientesAdminController clientesController;
    @FXML
    private TarjetasAdminController tarjetasController;
    @FXML
    private CancelarFuncionAdminController cancelarFuncionController;
    @FXML
    private EstadisticasAdminController estadisticasController;

    private final Map<Tab, PestanaAdmin> pestanas = new HashMap<>();
    private Usuario administrador;

    /**
     * Recibe el administrador autenticado y prepara todas las pestañas.
     */
    public void initData(Usuario usuario) {
        this.administrador = usuario;
        lblBienvenida.setText("Administración - " + usuario.getNombre());

        pestanas.put(tabPeliculas, peliculasController);
        pestanas.put(tabSalas, salasController);
        pestanas.put(tabFunciones, funcionesController);
        pestanas.put(tabCatalogo, catalogoController);
        pestanas.put(tabPromociones, promocionesController);
        pestanas.put(tabClientes, clientesController);
        pestanas.put(tabTarjetas, tarjetasController);
        pestanas.put(tabCancelarFuncion, cancelarFuncionController);
        pestanas.put(tabEstadisticas, estadisticasController);
        for (PestanaAdmin pestana : pestanas.values()) {
            pestana.inicializar(this);
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nueva) -> refrescar(nueva));
        refrescar(tabPane.getSelectionModel().getSelectedItem());
    }

    private void refrescar(Tab tab) {
        PestanaAdmin pestana = pestanas.get(tab);
        if (pestana != null) {
            pestana.refrescar();
        }
    }

    public Usuario getAdministrador() {
        return administrador;
    }

    @FXML
    private void cerrarSesion() {
        NavegadorVistas.cambiarVista(NavegadorVistas.ventanaDe(tabPane), NavegadorVistas.LOGIN, "Iniciar sesión");
    }
}
