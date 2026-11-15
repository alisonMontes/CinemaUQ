package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.exception.AccesoDenegadoException;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.facade.ProcesoCompraFacade;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Usuario;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;
import org.example.proyectofinalprogramacion2.strategy.SelectorReembolso;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cancelación de compras (cliente) y de funciones (administrador).
 * Todo el proceso lo ejecuta ProcesoCompraFacade; aquí solo se verifican permisos.
 */
// SRP: solo verifica quién cancela y delega la cancelación en la fachada.
public class CancelacionService {

    private final ProcesoCompraFacade procesoCompra;
    private final SelectorReembolso selectorReembolso;

    public CancelacionService() {
        this(new ProcesoCompraFacade(), new SelectorReembolso());
    }

    public CancelacionService(ProcesoCompraFacade procesoCompra, SelectorReembolso selectorReembolso) {
        this.procesoCompra = procesoCompra;
        this.selectorReembolso = selectorReembolso;
    }

    /**
     * Política que se aplicaría si el cliente cancela ahora (para mostrar el porcentaje antes de confirmar).
     */
    public ReembolsoStrategy consultarPolitica(Compra compra, LocalDateTime ahora) {
        return selectorReembolso.seleccionar(compra.getFuncion(), ahora);
    }

    /**
     * El cliente cancela una de sus compras.
     *
     * @return valor devuelto a la tarjeta con que se pagó
     */
    public double cancelarCompra(Cliente cliente, Compra compra, LocalDateTime ahora) throws CinemaUQException {
        if (!compra.getCliente().equals(cliente)) {
            throw new AccesoDenegadoException("La compra " + compra.getCodigo() + " no pertenece al cliente");
        }
        return procesoCompra.cancelarCompra(compra, ahora);
    }

    /**
     * El administrador cancela una función; se reembolsa el 100 % de las compras pagadas con tarjeta.
     *
     * @return compras reembolsadas
     */
    public List<Compra> cancelarFuncion(Usuario actor, Funcion funcion, LocalDateTime ahora) throws CinemaUQException {
        ValidadorAcceso.exigirAdministrador(actor);
        return procesoCompra.cancelarFuncion(funcion, ahora);
    }
}
