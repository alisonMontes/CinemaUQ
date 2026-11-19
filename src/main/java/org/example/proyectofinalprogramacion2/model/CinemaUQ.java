package org.example.proyectofinalprogramacion2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton: clase principal del dominio, con una única instancia que guarda en memoria
 * todos los datos del cine y es compartida por toda la aplicación.
 */
// SRP: solo almacena y busca los datos; las reglas de negocio están en la fachada, estrategias y estados.
public class CinemaUQ {

    private static CinemaUQ instancia;

    private String nombre;
    private int consecutivoCompras;
    private int consecutivoTarjetas;
    private final List<Usuario> usuarios;
    private final List<Pelicula> peliculas;
    private final List<Sala> salas;
    private final List<Funcion> funciones;
    private final List<Producto> productos;
    private final List<Combo> combos;
    private final List<Promocion> promociones;
    private final List<Compra> compras;

    private CinemaUQ() {
        this.nombre = "CinemaUQ";
        this.usuarios = new ArrayList<>();
        this.peliculas = new ArrayList<>();
        this.salas = new ArrayList<>();
        this.funciones = new ArrayList<>();
        this.productos = new ArrayList<>();
        this.combos = new ArrayList<>();
        this.promociones = new ArrayList<>();
        this.compras = new ArrayList<>();
    }

    /** Devuelve la instancia única, creándola la primera vez que se pide. */
    public static CinemaUQ getInstancia() {
        if (instancia == null) {
            instancia = new CinemaUQ();
        }
        return instancia;
    }

    /**
     * Borra todos los datos de la instancia única (usado por las pruebas).
     */
    public void reiniciar() {
        usuarios.clear();
        peliculas.clear();
        salas.clear();
        funciones.clear();
        productos.clear();
        combos.clear();
        promociones.clear();
        compras.clear();
        consecutivoCompras = 0;
        consecutivoTarjetas = 0;
    }

    /**
     * Número consecutivo para una nueva tarjeta virtual, por ejemplo "TV-0003".
     */
    public String generarNumeroTarjeta() {
        consecutivoTarjetas++;
        return String.format("TV-%04d", consecutivoTarjetas);
    }

    /**
     * Código consecutivo para una nueva compra, por ejemplo "C-0007".
     */
    public String generarCodigoCompra() {
        consecutivoCompras++;
        return String.format("C-%04d", consecutivoCompras);
    }

    // ---------- Usuarios ----------

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(Validador.noNulo(usuario, "usuario"));
    }

    /** Busca un usuario por correo sin distinguir mayúsculas; devuelve null si no existe. */
    public Usuario buscarUsuarioPorCorreo(String correo) {
        if (correo == null) {
            return null;
        }
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo.trim())) {
                return usuario;
            }
        }
        return null;
    }

    public List<Cliente> getClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Cliente) {
                clientes.add((Cliente) usuario);
            }
        }
        return clientes;
    }

    public List<Administrador> getAdministradores() {
        List<Administrador> administradores = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Administrador) {
                administradores.add((Administrador) usuario);
            }
        }
        return administradores;
    }

    // ---------- Catálogo ----------

    public void agregarPelicula(Pelicula pelicula) {
        peliculas.add(Validador.noNulo(pelicula, "película"));
    }

    public boolean eliminarPelicula(Pelicula pelicula) {
        return peliculas.remove(pelicula);
    }

    public Pelicula buscarPelicula(String codigo) {
        for (Pelicula pelicula : peliculas) {
            if (pelicula.getCodigo().equals(codigo)) {
                return pelicula;
            }
        }
        return null;
    }

    public void agregarSala(Sala sala) {
        salas.add(Validador.noNulo(sala, "sala"));
    }

    public boolean eliminarSala(Sala sala) {
        return salas.remove(sala);
    }

    public Sala buscarSala(String codigo) {
        for (Sala sala : salas) {
            if (sala.getCodigo().equals(codigo)) {
                return sala;
            }
        }
        return null;
    }

    public void agregarFuncion(Funcion funcion) {
        funciones.add(Validador.noNulo(funcion, "función"));
    }

    public boolean eliminarFuncion(Funcion funcion) {
        return funciones.remove(funcion);
    }

    public Funcion buscarFuncion(String codigo) {
        for (Funcion funcion : funciones) {
            if (funcion.getCodigo().equals(codigo)) {
                return funcion;
            }
        }
        return null;
    }

    public void agregarProducto(Producto producto) {
        productos.add(Validador.noNulo(producto, "producto"));
    }

    public boolean eliminarProducto(Producto producto) {
        return productos.remove(producto);
    }

    public Producto buscarProducto(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equals(codigo)) {
                return producto;
            }
        }
        return null;
    }

    public void agregarCombo(Combo combo) {
        combos.add(Validador.noNulo(combo, "combo"));
    }

    public boolean eliminarCombo(Combo combo) {
        return combos.remove(combo);
    }

    public Combo buscarCombo(String codigo) {
        for (Combo combo : combos) {
            if (combo.getCodigo().equals(codigo)) {
                return combo;
            }
        }
        return null;
    }

    public void agregarPromocion(Promocion promocion) {
        promociones.add(Validador.noNulo(promocion, "promoción"));
    }

    public boolean eliminarPromocion(Promocion promocion) {
        return promociones.remove(promocion);
    }

    public Promocion buscarPromocion(String codigo) {
        for (Promocion promocion : promociones) {
            if (promocion.getCodigo().equals(codigo)) {
                return promocion;
            }
        }
        return null;
    }

    // ---------- Compras ----------

    public void agregarCompra(Compra compra) {
        compras.add(Validador.noNulo(compra, "compra"));
    }

    public Compra buscarCompra(String codigo) {
        for (Compra compra : compras) {
            if (compra.getCodigo().equals(codigo)) {
                return compra;
            }
        }
        return null;
    }

    // ---------- Getters ----------

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validador.texto(nombre, "nombre");
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }

    public List<Sala> getSalas() {
        return Collections.unmodifiableList(salas);
    }

    public List<Funcion> getFunciones() {
        return Collections.unmodifiableList(funciones);
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public List<Combo> getCombos() {
        return Collections.unmodifiableList(combos);
    }

    public List<Promocion> getPromociones() {
        return Collections.unmodifiableList(promociones);
    }

    public List<Compra> getCompras() {
        return Collections.unmodifiableList(compras);
    }

    @Override
    public String toString() {
        return "CinemaUQ{nombre='" + nombre + "', usuarios=" + usuarios.size() + ", peliculas=" + peliculas.size()
                + ", salas=" + salas.size() + ", funciones=" + funciones.size() + ", compras=" + compras.size() + "}";
    }
}
