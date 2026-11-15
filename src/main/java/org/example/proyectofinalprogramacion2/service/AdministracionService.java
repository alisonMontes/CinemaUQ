package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.exception.AccesoDenegadoException;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Promocion;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestión del catálogo (películas, salas, asientos, funciones, productos, combos y promociones)
 * y consulta de clientes y compras. Todas las operaciones exigen un administrador.
 * La cancelación de funciones está en CancelacionService.
 */
// SRP: solo administra el catálogo del cine y las consultas del administrador.
public class AdministracionService {

    private final CinemaUQ cinema;

    public AdministracionService() {
        this(CinemaUQ.getInstancia());
    }

    public AdministracionService(CinemaUQ cinema) {
        this.cinema = cinema;
    }

    // ---------- Películas ----------

    public List<Pelicula> listarPeliculas(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getPeliculas();
    }

    public void crearPelicula(Usuario actor, Pelicula pelicula) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        if (cinema.buscarPelicula(pelicula.getCodigo()) != null) {
            throw new CinemaUQException("Ya existe una película con el código " + pelicula.getCodigo());
        }
        cinema.agregarPelicula(pelicula);
    }

    public void actualizarPelicula(Usuario actor, String codigo, String titulo, String genero, int duracionMinutos,
                                   String clasificacion, String sinopsis, boolean enCartelera)
            throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Pelicula pelicula = obtenerPelicula(codigo);
        pelicula.setTitulo(titulo);
        pelicula.setGenero(genero);
        pelicula.setDuracionMinutos(duracionMinutos);
        pelicula.setClasificacion(clasificacion);
        pelicula.setSinopsis(sinopsis);
        pelicula.setEnCartelera(enCartelera);
    }

    /**
     * Solo se elimina si no tiene funciones; si las tiene, se retira de cartelera.
     */
    public void eliminarPelicula(Usuario actor, String codigo) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Pelicula pelicula = obtenerPelicula(codigo);
        for (Funcion funcion : cinema.getFunciones()) {
            if (funcion.getPelicula().equals(pelicula)) {
                throw new CinemaUQException("La película tiene funciones registradas; retírela de cartelera");
            }
        }
        cinema.eliminarPelicula(pelicula);
    }

    // ---------- Salas y asientos ----------

    public List<Sala> listarSalas(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getSalas();
    }

    public void crearSala(Usuario actor, Sala sala) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        if (cinema.buscarSala(sala.getCodigo()) != null) {
            throw new CinemaUQException("Ya existe una sala con el código " + sala.getCodigo());
        }
        cinema.agregarSala(sala);
    }

    public void actualizarSala(Usuario actor, String codigo, String nombre) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        obtenerSala(codigo).setNombre(nombre);
    }

    public void eliminarSala(Usuario actor, String codigo) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Sala sala = obtenerSala(codigo);
        for (Funcion funcion : cinema.getFunciones()) {
            if (funcion.getSala().equals(sala)) {
                throw new CinemaUQException("La sala tiene funciones registradas");
            }
        }
        cinema.eliminarSala(sala);
    }

    public void agregarAsiento(Usuario actor, String codigoSala, Asiento asiento) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Sala sala = obtenerSala(codigoSala);
        if (sala.buscarAsiento(asiento.getFila(), asiento.getNumero()) != null) {
            throw new CinemaUQException("El asiento " + asiento.getCodigo() + " ya existe en la sala");
        }
        sala.agregarAsiento(asiento);
    }

    /**
     * No se elimina un asiento ocupado en una función programada.
     */
    public void eliminarAsiento(Usuario actor, String codigoSala, String fila, int numero) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Sala sala = obtenerSala(codigoSala);
        Asiento asiento = sala.buscarAsiento(fila, numero);
        if (asiento == null) {
            throw new CinemaUQException("No existe el asiento " + fila + numero + " en la sala");
        }
        for (Funcion funcion : cinema.getFunciones()) {
            if (funcion.getSala().equals(sala) && funcion.getEstado() == EstadoFuncion.PROGRAMADA
                    && funcion.getAsientosOcupados().contains(asiento)) {
                throw new CinemaUQException("El asiento " + asiento.getCodigo()
                        + " está vendido en la función " + funcion.getCodigo());
            }
        }
        sala.eliminarAsiento(asiento);
    }

    // ---------- Funciones ----------

    public List<Funcion> listarFunciones(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getFunciones();
    }

    /**
     * La película y la sala deben existir y la función no puede cruzarse con otra no cancelada de la misma sala.
     */
    public void crearFuncion(Usuario actor, Funcion funcion) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        if (cinema.buscarFuncion(funcion.getCodigo()) != null) {
            throw new CinemaUQException("Ya existe una función con el código " + funcion.getCodigo());
        }
        if (cinema.buscarPelicula(funcion.getPelicula().getCodigo()) == null
                || cinema.buscarSala(funcion.getSala().getCodigo()) == null) {
            throw new CinemaUQException("La película y la sala deben estar registradas");
        }
        validarSinCruce(funcion.getSala(), funcion.getPelicula(), funcion.getFechaHora(), funcion);
        cinema.agregarFuncion(funcion);
    }

    /**
     * Solo se reprograma una función PROGRAMADA sin asientos vendidos.
     */
    public void actualizarFuncion(Usuario actor, String codigo, LocalDate fecha, LocalTime hora, double precioBase)
            throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Funcion funcion = obtenerFuncion(codigo);
        if (funcion.getEstado() != EstadoFuncion.PROGRAMADA || !funcion.getAsientosOcupados().isEmpty()) {
            throw new CinemaUQException("Solo se modifica una función programada sin asientos vendidos");
        }
        validarSinCruce(funcion.getSala(), funcion.getPelicula(), LocalDateTime.of(fecha, hora), funcion);
        funcion.setFecha(fecha);
        funcion.setHora(hora);
        funcion.setPrecioBase(precioBase);
    }

    /**
     * Solo se elimina una función sin compras; si tiene compras se debe cancelar.
     */
    public void eliminarFuncion(Usuario actor, String codigo) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Funcion funcion = obtenerFuncion(codigo);
        for (Compra compra : cinema.getCompras()) {
            if (funcion.equals(compra.getFuncion())) {
                throw new CinemaUQException("La función tiene compras; debe cancelarse en lugar de eliminarse");
            }
        }
        cinema.eliminarFuncion(funcion);
    }

    private void validarSinCruce(Sala sala, Pelicula pelicula, LocalDateTime inicio, Funcion actual)
            throws CinemaUQException {
        LocalDateTime fin = inicio.plusMinutes(pelicula.getDuracionMinutos());
        for (Funcion otra : cinema.getFunciones()) {
            if (otra == actual || !otra.getSala().equals(sala) || otra.getEstado() == EstadoFuncion.CANCELADA) {
                continue;
            }
            LocalDateTime otraInicio = otra.getFechaHora();
            LocalDateTime otraFin = otraInicio.plusMinutes(otra.getPelicula().getDuracionMinutos());
            if (inicio.isBefore(otraFin) && otraInicio.isBefore(fin)) {
                throw new CinemaUQException("El horario se cruza con la función " + otra.getCodigo()
                        + " en " + sala.getNombre());
            }
        }
    }

    // ---------- Productos ----------

    public List<Producto> listarProductos(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getProductos();
    }

    public void crearProducto(Usuario actor, Producto producto) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        if (cinema.buscarProducto(producto.getCodigo()) != null) {
            throw new CinemaUQException("Ya existe un producto con el código " + producto.getCodigo());
        }
        cinema.agregarProducto(producto);
    }

    public void actualizarProducto(Usuario actor, String codigo, String nombre, double precio, String descripcion)
            throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Producto producto = obtenerProducto(codigo);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);
    }

    /**
     * No se elimina un producto que forma parte de un combo.
     */
    public void eliminarProducto(Usuario actor, String codigo) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Producto producto = obtenerProducto(codigo);
        for (Combo combo : cinema.getCombos()) {
            if (combo.getComponentes().contains(producto)) {
                throw new CinemaUQException("El producto forma parte del combo " + combo.getNombre());
            }
        }
        cinema.eliminarProducto(producto);
    }

    // ---------- Combos ----------

    public List<Combo> listarCombos(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getCombos();
    }

    public void crearCombo(Usuario actor, Combo combo) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        if (cinema.buscarCombo(combo.getCodigo()) != null) {
            throw new CinemaUQException("Ya existe un combo con el código " + combo.getCodigo());
        }
        if (combo.getComponentes().isEmpty()) {
            throw new CinemaUQException("El combo debe tener al menos un componente");
        }
        cinema.agregarCombo(combo);
    }

    public void actualizarCombo(Usuario actor, String codigo, String nombre, double porcentajeDescuento,
                                List<ItemVendible> componentes) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Combo combo = obtenerCombo(codigo);
        if (componentes == null || componentes.isEmpty()) {
            throw new CinemaUQException("El combo debe tener al menos un componente");
        }
        combo.setNombre(nombre);
        combo.setPorcentajeDescuento(porcentajeDescuento);
        for (ItemVendible componente : new ArrayList<>(combo.getComponentes())) {
            combo.eliminarComponente(componente);
        }
        for (ItemVendible componente : componentes) {
            combo.agregarComponente(componente);
        }
    }

    /**
     * No se elimina un combo que forma parte de otro combo.
     */
    public void eliminarCombo(Usuario actor, String codigo) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Combo combo = obtenerCombo(codigo);
        for (Combo otro : cinema.getCombos()) {
            if (otro != combo && otro.getComponentes().contains(combo)) {
                throw new CinemaUQException("El combo forma parte del combo " + otro.getNombre());
            }
        }
        cinema.eliminarCombo(combo);
    }

    // ---------- Promociones ----------

    public List<Promocion> listarPromociones(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getPromociones();
    }

    public void crearPromocion(Usuario actor, Promocion promocion) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        if (cinema.buscarPromocion(promocion.getCodigo()) != null) {
            throw new CinemaUQException("Ya existe una promoción con el código " + promocion.getCodigo());
        }
        cinema.agregarPromocion(promocion);
    }

    public void actualizarPromocion(Usuario actor, String codigo, String nombre, double porcentajeDescuento,
                                    LocalDate fechaInicio, LocalDate fechaFin) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        Promocion promocion = obtenerPromocion(codigo);
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new CinemaUQException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        promocion.setNombre(nombre);
        promocion.setPorcentajeDescuento(porcentajeDescuento);
        // El orden evita que la validación cruzada de Promocion falle con las fechas anteriores.
        if (fechaInicio.isAfter(promocion.getFechaFin())) {
            promocion.setFechaFin(fechaFin);
            promocion.setFechaInicio(fechaInicio);
        } else {
            promocion.setFechaInicio(fechaInicio);
            promocion.setFechaFin(fechaFin);
        }
    }

    public void eliminarPromocion(Usuario actor, String codigo) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        cinema.eliminarPromocion(obtenerPromocion(codigo));
    }

    // ---------- Consultas ----------

    public List<Cliente> consultarClientes(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getClientes();
    }

    public List<Compra> consultarCompras(Usuario actor) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cinema.getCompras();
    }

    public List<Compra> consultarComprasDeCliente(Usuario actor, Cliente cliente) throws AccesoDenegadoException {
        ValidadorAcceso.exigirAdministrador(actor);
        return cliente.getCompras();
    }

    // ---------- Búsquedas obligatorias ----------

    private Pelicula obtenerPelicula(String codigo) throws CinemaUQException {
        Pelicula pelicula = cinema.buscarPelicula(codigo);
        if (pelicula == null) {
            throw new CinemaUQException("No existe la película " + codigo);
        }
        return pelicula;
    }

    private Sala obtenerSala(String codigo) throws CinemaUQException {
        Sala sala = cinema.buscarSala(codigo);
        if (sala == null) {
            throw new CinemaUQException("No existe la sala " + codigo);
        }
        return sala;
    }

    private Funcion obtenerFuncion(String codigo) throws CinemaUQException {
        Funcion funcion = cinema.buscarFuncion(codigo);
        if (funcion == null) {
            throw new CinemaUQException("No existe la función " + codigo);
        }
        return funcion;
    }

    private Producto obtenerProducto(String codigo) throws CinemaUQException {
        Producto producto = cinema.buscarProducto(codigo);
        if (producto == null) {
            throw new CinemaUQException("No existe el producto " + codigo);
        }
        return producto;
    }

    private Combo obtenerCombo(String codigo) throws CinemaUQException {
        Combo combo = cinema.buscarCombo(codigo);
        if (combo == null) {
            throw new CinemaUQException("No existe el combo " + codigo);
        }
        return combo;
    }

    private Promocion obtenerPromocion(String codigo) throws CinemaUQException {
        Promocion promocion = cinema.buscarPromocion(codigo);
        if (promocion == null) {
            throw new CinemaUQException("No existe la promoción " + codigo);
        }
        return promocion;
    }
}
