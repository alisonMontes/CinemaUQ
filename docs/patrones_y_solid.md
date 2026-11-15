# CinemaUQ – Patrones de diseño y principios SOLID

Se implementan 10 patrones GoF en su versión clásica. Cada uno resuelve una necesidad real del sistema.
Las rutas son relativas a `src/main/java/org/example/proyectofinalprogramacion2/`.
Los diagramas están en `docs/diagramas_patrones/`.

| # | Patrón | Tipo | Clase(s) principal(es) |
|---|---|---|---|
| 1 | Factory Method | Creacional | `factory/EntradaFactory` |
| 2 | Builder | Creacional | `model/Compra.Builder` |
| 3 | Singleton | Creacional | `model/CinemaUQ` |
| 4 | Composite | Estructural | `model/Combo` |
| 5 | Decorator | Estructural | `decorator/PromocionDecorator` |
| 6 | Facade | Estructural | `facade/ProcesoCompraFacade` |
| 7 | Adapter | Estructural | `adapter/PagoTarjetaAdapter` |
| 8 | Strategy | Comportamiento | `strategy/ReembolsoStrategy` |
| 9 | State | Comportamiento | `model/EstadoCompraState` |
| 10 | Observer | Comportamiento | `observer/ObservadorFuncion` |

---

## 1. Factory Method

- **Problema identificado:** al elegir un asiento hay que crear la entrada correcta: general para un asiento NORMAL y accesible para uno ACCESIBLE. Si cada pantalla hiciera `new EntradaGeneral(...)` o `new EntradaAccesible(...)` con `if`, la decisión quedaría repetida y acoplada a las clases concretas.
- **Patrón utilizado:** Factory Method.
- **Justificación:** una fábrica abstracta declara `crearEntrada()` y cada fábrica concreta decide qué subclase instanciar. El código cliente pide la fábrica con `EntradaFactory.segunAsiento(asiento)` y trabaja con el tipo `Entrada`. La validación del tipo de asiento ya existe en los constructores de `EntradaGeneral` y `EntradaAccesible`, que lanzan `TipoAsientoInvalidoException`. Las fábricas no la repiten.
- **Clases involucradas:** `EntradaFactory` (creador abstracto), `EntradaGeneralFactory` y `EntradaAccesibleFactory` (creadores concretos), `Entrada` (producto), `EntradaGeneral` y `EntradaAccesible` (productos concretos), `TipoAsientoInvalidoException`.
- **Diagrama:** `docs/diagramas_patrones/01_factory_method.puml`
- **Evidencia:**
  - `factory/EntradaFactory.java`: `crearEntrada()` abstracto y `segunAsiento()`.
  - `factory/EntradaGeneralFactory.java` y `factory/EntradaAccesibleFactory.java`.
  - `model/EntradaGeneral.java` y `model/EntradaAccesible.java`: validación en el constructor.

## 2. Builder

- **Problema identificado:** una compra tiene muchas partes opcionales (función, varias entradas, productos, combos, promoción, si es redención) y reglas que solo se pueden revisar al final: al menos una entrada, entradas de la misma función, promoción vigente y total calculado. Un constructor con muchos parámetros sería ilegible y permitiría compras incompletas.
- **Patrón utilizado:** Builder, como clase interna estática `Compra.Builder`. El constructor de `Compra` es privado.
- **Justificación:** el cliente agrega las partes paso a paso con métodos encadenados. `build()` valida todo, calcula el total (aplicando la promoción con `PromocionDecorator`) y entrega la compra en estado PENDIENTE. Una vez construida, la compra no se puede modificar: no tiene setters de contenido.
- **Clases involucradas:** `Compra`, `Compra.Builder`, `CompraPendiente`, `PromocionDecorator`.
- **Diagrama:** `docs/diagramas_patrones/02_builder.puml`
- **Evidencia:** `model/Compra.java`, que contiene el constructor privado `Compra(Builder, double)`, la clase `public static class Builder` y los métodos `build()`, `validarCompraConTarjeta()`, `validarRedencion()` y `calcularTotal()`.

```java
Compra compra = new Compra.Builder(cinema.generarCodigoCompra(), cliente, ahora)
        .funcion(funcion)
        .agregarEntrada(EntradaFactory.segunAsiento(asiento).crearEntrada(funcion, asiento))
        .agregarCombo(combo)
        .promocion(promocion)
        .build();
```

## 3. Singleton

- **Problema identificado:** todos los datos viven en memoria. Si cada controlador creara su propio `CinemaUQ`, cada pantalla vería datos distintos; por ejemplo, el administrador recargaría una tarjeta que el cliente no vería.
- **Patrón utilizado:** Singleton.
- **Justificación:** el constructor es privado y `getInstancia()` crea la instancia la primera vez y luego devuelve siempre la misma. `reiniciar()` limpia sus listas y el consecutivo de compras, para que cada prueba empiece desde cero sin crear otra instancia.
- **Clases involucradas:** `CinemaUQ`; quien la consume es `ProcesoCompraFacade`.
- **Diagrama:** `docs/diagramas_patrones/03_singleton.puml`
- **Evidencia:** `model/CinemaUQ.java`, con el atributo `private static CinemaUQ instancia`, el constructor `private CinemaUQ()` y los métodos `getInstancia()`, `reiniciar()` y `generarCodigoCompra()`.

## 4. Composite

- **Problema identificado:** un combo agrupa productos y su precio depende de ellos. Si el precio se escribiera a mano, quedaría desactualizado al cambiar el precio de un producto. Además, la compra debe tratar un combo igual que un producto.
- **Patrón utilizado:** Composite.
- **Justificación:** `Combo` implementa `ItemVendible` y contiene una lista de `ItemVendible` (productos u otros combos). Su `getPrecio()` recorre los componentes y resta el `porcentajeDescuento` propio del combo. La compra suma `getPrecio()` sin distinguir hojas (`Producto`) de compuestos (`Combo`). Se impide agregar entradas y que el combo se contenga a sí mismo.
- **Clases involucradas:** `ItemVendible` (componente), `Producto` (hoja), `Combo` (compuesto).
- **Diagrama:** `docs/diagramas_patrones/04_composite.puml`
- **Evidencia:** `model/Combo.java`, con `componentes : List<ItemVendible>`, `agregarComponente()`, `getPrecioRegular()`, `getPrecio()` y `porcentajeDescuento`.

## 5. Decorator

- **Problema identificado:** las promociones cambian el precio de lo que se vende solo mientras están vigentes. Meter esa lógica dentro de `Entrada`, `Producto` y `Combo` los llenaría de condiciones de fecha y descuentos.
- **Patrón utilizado:** Decorator.
- **Justificación:** `PromocionDecorator` implementa `ItemVendible` y envuelve otro `ItemVendible`. Si la promoción está vigente en la fecha indicada, devuelve el precio con el descuento y el nombre con la promoción; si no, delega sin cambios. El ítem original no se modifica. `Compra.Builder` lo usa para calcular el total.
- **Clases involucradas:** `ItemVendible`, `PromocionDecorator`, `Promocion`, `Entrada`, `Producto`, `Combo`, `Compra.Builder`.
- **Diagrama:** `docs/diagramas_patrones/05_decorator.puml`
- **Evidencia:**
  - `decorator/PromocionDecorator.java`: `getPrecio()` y `aplicaDescuento()`.
  - `model/Compra.java`: `Builder.calcularTotal()`.

## 6. Facade

- **Problema identificado:** comprar implica muchos pasos sobre muchas clases: validar la función y los asientos, validar la tarjeta, pasar la compra a PAGADA, cobrar y registrar el movimiento, ocupar los asientos, acumular puntos, registrar la compra y suscribir al cliente a la función. Cancelar también: elegir la política, reembolsar, liberar asientos, anular puntos y dejar de observar. Si los controladores de JavaFX hicieran todo esto, quedarían acoplados a todo el modelo.
- **Patrón utilizado:** Facade.
- **Justificación:** `ProcesoCompraFacade` ofrece tres operaciones (`comprar`, `cancelarCompra`, `cancelarFuncion`) y coordina el orden correcto. Primero valida todo y luego modifica, para no cobrar si algo falla.
- **Clases involucradas:** `ProcesoCompraFacade`, `Compra`, `Funcion`, `MedioPago`, `CuentaPuntos`, `AcumulacionPuntos`, `Cliente`, `CinemaUQ`, `SelectorReembolso`, `ReembolsoStrategy`.
- **Diagrama:** `docs/diagramas_patrones/06_facade.puml`
- **Evidencia:** `facade/ProcesoCompraFacade.java`, con los métodos `comprar()`, `cancelarCompra()` y `cancelarFuncion()`, y los pasos privados `validarFuncionDisponible()`, `validarAsientos()`, `acumularPuntos()` y `reembolsar()`.

## 7. Adapter

- **Problema identificado:** el proceso de compra necesita un medio de pago con `validarPago`, `cobrar` y `reembolsar`. `TarjetaVirtual` es una entidad de datos (saldo, estado y lista de movimientos) que no tiene esas operaciones. Si la fachada manejara directamente saldos y movimientos, quedaría atada a la tarjeta.
- **Patrón utilizado:** Adapter (de objeto).
- **Justificación:** `PagoTarjetaAdapter` implementa `MedioPago` (la interfaz objetivo) y traduce cada operación a la tarjeta (la clase adaptada). Verifica que esté ACTIVA (`TarjetaInactivaException`) y que tenga saldo (`SaldoInsuficienteException`), descuenta o abona y registra el `MovimientoTarjeta` (PAGO o REEMBOLSO). La compra guarda su `MedioPago`, así el reembolso vuelve a la misma tarjeta.
- **Clases involucradas:** `MedioPago` (objetivo), `PagoTarjetaAdapter` (adaptador), `TarjetaVirtual` (adaptada), `ProcesoCompraFacade` (cliente), `Compra`.
- **Diagrama:** `docs/diagramas_patrones/07_adapter.puml`
- **Evidencia:** `adapter/MedioPago.java` y `adapter/PagoTarjetaAdapter.java`.

## 8. Strategy

- **Problema identificado:** el valor a devolver al cancelar depende de una política que cambia según el caso: más de 24 h, 100 %; desde 2 h hasta 24 h inclusive, 80 %; menos de 2 h, no se permite; función cancelada por el cine, 100 %. Un `if/else` dentro del proceso de cancelación mezclaría la decisión con el cálculo y obligaría a modificarlo para cada política nueva.
- **Patrón utilizado:** Strategy.
- **Justificación:** cada política es una clase que implementa `ReembolsoStrategy`. `SelectorReembolso` elige la estrategia según el tiempo restante (con `Duration`) o el estado de la función. La compra y la fachada solo usan la interfaz. `SinReembolso` lanza `CancelacionNoPermitidaException`.
- **Clases involucradas:** `ReembolsoStrategy`, `ReembolsoCompleto`, `ReembolsoParcial`, `SinReembolso`, `SelectorReembolso`, `ProcesoCompraFacade`, `Compra`.
- **Diagrama:** `docs/diagramas_patrones/08_strategy.puml`
- **Evidencia:** los archivos de `strategy/`: `ReembolsoStrategy.java`, `ReembolsoCompleto.java`, `ReembolsoParcial.java`, `SinReembolso.java` y `SelectorReembolso.java`.

## 9. State

- **Problema identificado:** lo que se puede hacer con una compra depende de su estado. Pendiente se puede pagar pero no reembolsar; pagada se puede reembolsar (salvo si fue con puntos) pero no pagar otra vez; reembolsada no admite nada más. Con el enum y `if`, estas reglas quedarían dispersas y sería fácil permitir un doble reembolso.
- **Patrón utilizado:** State.
- **Justificación:** `Compra` delega en su objeto `EstadoCompraState`. Cada estado decide la operación y hace la transición con `cambiarEstado()`, que es visible solo dentro del paquete `model`. El enum `EstadoCompra` se conserva para mostrar y reportar. No se guarda aparte: `Compra.getEstado()` devuelve `estado.getValor()`, así que existe en un solo sitio. `Funcion` sigue usando solo el enum `EstadoFuncion`.
- **Transiciones:**

  | Estado | `pagar()` | `reembolsar()` |
  |---|---|---|
  | `CompraPendiente` | pasa a PAGADA | `CancelacionNoPermitidaException` |
  | `CompraPagada` | `CinemaUQException` | con puntos: `CancelacionNoPermitidaException`; con tarjeta: aplica la estrategia y pasa a REEMBOLSADA |
  | `CompraReembolsada` | `CinemaUQException` | `ReembolsoYaEjecutadoException` |

- **Clases involucradas:** `Compra`, `EstadoCompraState`, `CompraPendiente`, `CompraPagada`, `CompraReembolsada`, `EstadoCompra`.
- **Diagrama:** `docs/diagramas_patrones/09_state.puml` (clases y transiciones).
- **Evidencia:**
  - `model/EstadoCompraState.java`, `model/CompraPendiente.java`, `model/CompraPagada.java` y `model/CompraReembolsada.java`.
  - `model/Compra.java`: `confirmarPago()`, `confirmarCanje()`, `reembolsar()`, `cambiarEstado()` y `getEstado()`.

## 10. Observer

- **Problema identificado:** cuando el cine cancela una función, los clientes que compraron entradas deben enterarse. La función no debe conocer los detalles de cada cliente.
- **Patrón utilizado:** Observer, con una interfaz propia `ObservadorFuncion` (no se usa `java.util.Observable`, que está obsoleta).
- **Justificación:** `Funcion` es el sujeto: guarda una lista de `ObservadorFuncion` y, en `cancelar()`, llama a `notificarObservadores()`. `Cliente` implementa `ObservadorFuncion` y agrega el mensaje a su lista `notificaciones`. La fachada suscribe al cliente cuando compra y lo retira cuando reembolsa (si no le quedan compras pagadas en esa función). Las compras sin función (redenciones de producto o combo) no observan nada.
- **Clases involucradas:** `ObservadorFuncion`, `Funcion`, `Cliente`, `ProcesoCompraFacade`.
- **Diagrama:** `docs/diagramas_patrones/10_observer.puml`
- **Evidencia:**
  - `observer/ObservadorFuncion.java`.
  - `model/Funcion.java`: `agregarObservador()`, `eliminarObservador()`, `notificarObservadores()` y `cancelar()`.
  - `model/Cliente.java`: `actualizar()` y `getNotificaciones()`.
  - `facade/ProcesoCompraFacade.java`: `comprar()`, `reembolsar()` y `cancelarFuncion()`.

---

# Principios SOLID

Cada clase relevante tiene un comentario de una línea con el principio que aplica (por ejemplo, `// SRP: solo calcula el reembolso del 80 %.`).

## S – Responsabilidad única (SRP)

| Clase | Única responsabilidad |
|---|---|
| `ReembolsoCompleto`, `ReembolsoParcial`, `SinReembolso` | Calcular (o rechazar) un tipo de reembolso. |
| `SelectorReembolso` | Decidir qué estrategia aplica; no calcula montos. |
| `CompraPendiente`, `CompraPagada`, `CompraReembolsada` | Definir lo permitido en un estado. |
| `ProcesoCompraFacade` | Coordinar el orden de los pasos; cada paso lo ejecuta la clase dueña. |
| `PagoTarjetaAdapter` | Traducir operaciones de pago a cambios en la tarjeta. |
| `PromocionDecorator` | Aplicar el descuento de una promoción a un ítem. |
| `Compra.Builder` | Armar y validar la compra; no cobra ni ocupa asientos. |
| `EntradaGeneralFactory`, `EntradaAccesibleFactory` | Crear un tipo de entrada. |
| `CinemaUQ` | Almacenar y buscar datos; las reglas viven fuera. |
| `ConfiguracionPuntos` | Concentrar las constantes del programa de puntos. |

**Por qué:** cada cambio de regla toca una sola clase. Por ejemplo, si el 80 % pasa a ser 70 %, solo cambia `ReembolsoParcial`.

## O – Abierto/cerrado (OCP)

| Dónde | Cómo se extiende sin modificar |
|---|---|
| `ReembolsoStrategy` | Una política nueva (por ejemplo, 50 % entre 1 y 2 h) es una clase nueva que implementa la interfaz. `Compra`, los estados y la fachada no cambian; solo se agrega el caso en `SelectorReembolso`. |
| `EntradaFactory` y `Entrada` | Un tipo nuevo de entrada (por ejemplo, VIP) es una subclase de `Entrada` más su fábrica. Las entradas y fábricas existentes no cambian; solo se agrega el caso en `segunAsiento()`. |
| `Combo` | Acepta cualquier `ItemVendible` nuevo como componente sin modificarse. |
| `PromocionDecorator` | Cambia el precio de cualquier ítem sin tocar `Entrada`, `Producto` ni `Combo`. |
| `EstadoCompraState` | Un estado nuevo es otra clase que implementa la interfaz. |

## L – Sustitución de Liskov (LSP)

| Tipo base | Subtipos | Evidencia |
|---|---|---|
| `Usuario` | `Cliente`, `Administrador` | El inicio de sesión y los listados usan `verificarClave()` y `getRol()` sin saber el subtipo. |
| `Entrada` / `ItemVendible` | `EntradaGeneral`, `EntradaAccesible`, `Producto`, `Combo`, `PromocionDecorator` | `Compra.getItems()` y `calcularSubtotal()` suman `getPrecio()` sobre `ItemVendible` sin preguntar el tipo. |
| `EstadoCompraState` | `CompraPendiente`, `CompraPagada`, `CompraReembolsada` | `Compra` llama `estado.pagar()` y `estado.reembolsar()` sin conocer la clase concreta. |
| `ReembolsoStrategy` | `ReembolsoCompleto`, `ReembolsoParcial`, `SinReembolso` | `CompraPagada.reembolsar()` funciona con cualquiera. La excepción de `SinReembolso` está declarada en el contrato de la interfaz. |

## I – Segregación de interfaces (ISP)

| Interfaz | Métodos | Por qué es pequeña |
|---|---|---|
| `ItemVendible` | `getNombre()`, `getPrecio()` | Lo único que la compra necesita de lo que vende. |
| `MedioPago` | `validarPago()`, `cobrar()`, `reembolsar()`, `getDescripcion()` | Solo pagos. Recargar, bloquear y activar son tareas del administrador y no están aquí. |
| `ObservadorFuncion` | `actualizar()` | Un solo aviso. |
| `ReembolsoStrategy` | `calcularReembolso()`, `getDescripcion()` | Un solo cálculo. |
| `EstadoCompraState` | `getValor()`, `pagar()`, `reembolsar()` | Solo las operaciones que cambian el estado. |

## D – Inversión de dependencias (DIP)

- `ProcesoCompraFacade` recibe un `MedioPago` y trabaja con `ReembolsoStrategy`. Nunca usa `TarjetaVirtual` ni `ReembolsoCompleto`, `ReembolsoParcial` o `SinReembolso` directamente. Otro medio de pago (por ejemplo, un bono de regalo) funcionaría sin cambiar la fachada.
- `Compra` guarda un `MedioPago` (no una `TarjetaVirtual`) y su método `reembolsar()` recibe un `ReembolsoStrategy`.
- `Funcion` depende de `ObservadorFuncion`, no de `Cliente`.
- La fachada recibe por constructor `CinemaUQ` y `SelectorReembolso`. El constructor sin parámetros usa los valores por defecto (`CinemaUQ.getInstancia()`).
