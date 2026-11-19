package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.exception.CredencialesInvalidasException;
import org.example.proyectofinalprogramacion2.exception.UsuarioYaRegistradoException;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.TarjetaVirtual;
import org.example.proyectofinalprogramacion2.model.Usuario;

/**
 * Registro de clientes e inicio de sesión de clientes y administradores.
 */
// SRP: solo registra clientes y autentica usuarios.
public class AutenticacionService {

    private final CinemaUQ cinema;

    public AutenticacionService() {
        this(CinemaUQ.getInstancia());
    }

    public AutenticacionService(CinemaUQ cinema) {
        this.cinema = cinema;
    }

    /**
     * Registra un cliente con una tarjeta virtual ACTIVA con saldo 0 y una cuenta de puntos vacía.
     *
     * @throws UsuarioYaRegistradoException si ya existe un usuario con ese correo
     */
    public Cliente registrarCliente(String nombre, String correo, String clave) throws UsuarioYaRegistradoException {
        if (cinema.buscarUsuarioPorCorreo(correo) != null) {
            throw new UsuarioYaRegistradoException("Ya existe un usuario registrado con el correo " + correo);
        }
        TarjetaVirtual tarjeta = new TarjetaVirtual(cinema.generarNumeroTarjeta(), 0);
        Cliente cliente = new Cliente(nombre, correo, clave, tarjeta);
        cinema.agregarUsuario(cliente);
        return cliente;
    }

    /**
     * Inicia sesión de un cliente o administrador. La vista se elige con getRol().
     *
     * @throws CredencialesInvalidasException si el correo no existe o la clave no coincide
     */
    public Usuario iniciarSesion(String correo, String clave) throws CredencialesInvalidasException {
        Usuario usuario = cinema.buscarUsuarioPorCorreo(correo);
        if (usuario == null || !usuario.verificarClave(clave)) {
            throw new CredencialesInvalidasException("Correo o clave incorrectos");
        }
        return usuario;
    }
}
