# CinemaUQ – Análisis del sistema

Sistema de gestión de un cine. Proyecto Final de Programación II.
Toda la información vive en memoria y se carga como datos iniciales al arrancar.

---

## 1. Actores

| Actor | Descripción |
|---|---|
| **Cliente** | Persona registrada que consulta la cartelera, compra entradas, productos y combos, paga con su tarjeta virtual, cancela compras y redime puntos. |
| **Administrador** | Gestiona el catálogo del cine (películas, salas, asientos, funciones, productos, combos, promociones), consulta clientes y compras, cancela funciones, administra tarjetas y genera estadísticas y reportes. |
| **Reloj del sistema** (actor secundario) | Aporta la fecha y hora actual (`LocalDateTime.now()`) que la interfaz pasa a las reglas que dependen del tiempo. |

---

## 2. Requisitos funcionales

### Comunes
| ID | Requisito |
|---|---|
| RF-001 | El sistema debe permitir iniciar sesión con correo y clave, y dirigir al usuario a la vista de su perfil (Cliente o Administrador). |
| RF-002 | El sistema debe permitir cerrar sesión. |
| RF-003 | El sistema debe cargar al iniciar los datos de ejemplo: 1 administrador, 3 clientes con tarjeta (uno BLOQUEADA), 4 películas, 3 salas con asientos (algunos accesibles), funciones, productos, combos, promociones y puntos de ejemplo. |

### Cliente
| ID | Requisito |
|---|---|
| RF-004 | El cliente debe poder registrarse con nombre, correo y clave. Al registrarse recibe una tarjeta virtual ACTIVA con saldo 0 y una cuenta de puntos vacía. No se permiten dos usuarios con el mismo correo. |
| RF-005 | El cliente debe poder consultar la cartelera (películas en cartelera). |
| RF-006 | El cliente debe poder consultar las funciones programadas de una película (sala, fecha, hora, precio y asientos disponibles). |
| RF-007 | El cliente debe poder elegir asientos de forma gráfica en el mapa de la sala, diferenciando disponibles, ocupados, accesibles y seleccionados. |
| RF-008 | El cliente debe poder agregar productos y combos a su compra. |
| RF-009 | El cliente debe poder aplicar una promoción vigente a su compra. |
| RF-010 | El cliente debe poder ver el resumen de la compra (ítems, subtotal, descuento y total) antes de confirmarla. |
| RF-011 | El cliente debe poder confirmar la compra pagando con su tarjeta virtual. |
| RF-012 | El cliente debe poder consultar el saldo, el estado y los movimientos de su tarjeta. |
| RF-013 | El cliente debe poder consultar su historial de compras. |
| RF-014 | El cliente debe poder solicitar la cancelación de una compra pagada con tarjeta cuando las reglas lo permitan, viendo el porcentaje y el valor a reembolsar. |
| RF-015 | El cliente debe poder consultar sus puntos disponibles y el detalle de cada acumulación (fecha de obtención, vencimiento y saldo). |
| RF-016 | El cliente debe poder redimir puntos por una entrada (eligiendo función y asiento), un producto o un combo. |

### Administrador
| ID | Requisito |
|---|---|
| RF-017 | El administrador debe poder crear, consultar, modificar y eliminar películas. |
| RF-018 | El administrador debe poder crear, consultar, modificar y eliminar salas. |
| RF-019 | El administrador debe poder agregar y eliminar asientos de una sala, indicando fila, número y tipo (NORMAL o ACCESIBLE). |
| RF-020 | El administrador debe poder crear, consultar, modificar y eliminar funciones (película, sala, fecha, hora y precio base). |
| RF-021 | El administrador debe poder crear, consultar, modificar y eliminar productos. |
| RF-022 | El administrador debe poder crear, consultar, modificar y eliminar combos, eligiendo sus productos. |
| RF-023 | El administrador debe poder crear, consultar, modificar y eliminar promociones (porcentaje y vigencia). |
| RF-024 | El administrador debe poder consultar los clientes registrados con su tarjeta y sus puntos. |
| RF-025 | El administrador debe poder consultar todas las compras. |
| RF-026 | El administrador debe poder cancelar una función. Todas sus compras pagadas con tarjeta se reembolsan al 100 %. |
| RF-027 | El administrador debe poder recargar la tarjeta de un cliente. |
| RF-028 | El administrador debe poder activar o bloquear la tarjeta de un cliente. |

### Estadísticas y reportes
| ID | Requisito |
|---|---|
| RF-029 | El sistema debe mostrar estadísticas: ingresos totales, entradas vendidas por película, ocupación por función, productos y combos más vendidos, reembolsos realizados y puntos redimidos. |
| RF-030 | El sistema debe mostrar reportes visuales con gráficos de JavaFX (barras y torta). |
| RF-031 | El sistema debe exportar reportes en archivos TXT (ventas, ocupación y clientes). |

---

## 3. Reglas de negocio

### Del enunciado
| ID | Regla |
|---|---|
| RN-001 | Una función tiene película, sala, fecha y hora. La disponibilidad de un asiento se maneja por función: la función guarda sus asientos ocupados. |
| RN-002 | Un asiento no puede venderse dos veces en la misma función. |
| RN-003 | Toda compra pagada con tarjeta incluye al menos una entrada de una función; puede incluir productos y combos. |
| RN-004 | A una compra puede aplicarse una promoción vigente. |
| RN-005 | Antes de confirmar una compra se verifica que la tarjeta esté ACTIVA y tenga saldo suficiente. Si se aprueba, se descuenta el valor y se registra un movimiento PAGO. |
| RN-006 | Solo el administrador recarga tarjetas y puede activarlas o bloquearlas. Cada recarga se registra como movimiento RECARGA. |
| RN-007 | Cancelación según el tiempo que falta para la función: **más de 24 h → 100 %**; **desde 2 h hasta 24 h inclusive → 80 %**; **menos de 2 h → no se permite**. |
| RN-008 | Si el cine cancela la función, se reembolsa el 100 % de cada compra pagada con tarjeta. |
| RN-009 | El reembolso va a la tarjeta con la que se pagó, se registra como movimiento REEMBOLSO y no puede ejecutarse dos veces sobre la misma compra. |
| RN-010 | Los puntos se acumulan por compra pagada con tarjeta, guardando la fecha de obtención, y vencen al año. |
| RN-011 | Al redimir se usan primero los puntos más próximos a vencer. No se usan puntos vencidos y se verifica que alcancen antes de redimir. |
| RN-012 | La redención es una operación aparte: canjea un beneficio, no pasa por la tarjeta y no genera puntos. |
| RN-013 | Las compras pagadas con puntos no se cancelan. |
| RN-014 | Al reembolsar una compra se anulan los puntos que generó si siguen sin usar. Si ya se usaron, el reembolso procede igual. |

### Adicionales (decisiones de diseño)
| ID | Regla |
|---|---|
| RN-015 | El correo identifica al usuario: es único y no distingue mayúsculas de minúsculas. |
| RN-016 | El precio de una entrada es el precio base de la función, sea el asiento normal o accesible. Los precios base de las funciones de ejemplo están entre $16.000 y $20.000. |
| RN-017 | Un asiento accesible solo puede venderse como entrada accesible y uno normal como entrada general. |
| RN-018 | Solo se venden y redimen entradas de funciones PROGRAMADAS cuya hora de inicio aún no ha pasado. |
| RN-019 | Una promoción es vigente si la fecha actual está entre su fecha de inicio y su fecha de fin, inclusive. Su porcentaje es mayor que 0 y menor que 100. Se aplica sobre el subtotal de la compra; solo una promoción por compra. |
| RN-020 | Los puntos se calculan sobre el total efectivamente pagado (después del descuento), descartando fracciones. |
| RN-021 | Una acumulación vence el mismo día del año siguiente a su obtención: se puede usar hasta el día anterior. |
| RN-022 | Al reembolsar, los asientos de la compra se liberan en la función (salvo si la función fue cancelada, porque ya no se venden). |
| RN-023 | El monto de una recarga debe ser mayor que cero. El saldo de una tarjeta nunca es negativo. |
| RN-024 | Una tarjeta BLOQUEADA no puede pagar, pero sí recibe reembolsos y recargas. |
| RN-025 | Al registrarse, un cliente recibe una tarjeta ACTIVA con saldo 0; para comprar debe pedir una recarga al administrador. |
| RN-026 | Un asiento solo puede ocuparse si pertenece a la sala de la función. |
| RN-027 | Una compra pendiente (sin confirmar) no ocupa asientos. Los asientos se ocupan al confirmar el pago o la redención. |
| RN-028 | Las entradas canjeadas con puntos para una función que el cine cancela **no devuelven puntos**, porque no hay valor pagado ni dinero que regresar a la tarjeta. El cliente sí recibe la notificación de la cancelación. |
| RN-029 | Solo un administrador recarga, activa o bloquea tarjetas, cancela funciones y gestiona el catálogo; cualquier otro usuario recibe `AccesoDenegadoException`. Un cliente solo puede cancelar sus propias compras. |
| RN-030 | Una función se puede vender o canjear solo si está PROGRAMADA y no ha empezado; si no, `FuncionNoDisponibleException`. |
| RN-031 | Dos funciones no canceladas de la misma sala no pueden cruzarse en horario (inicio + duración de la película). |
| RN-032 | Solo se reprograma una función PROGRAMADA sin asientos vendidos. Una función con compras no se elimina: se cancela. |
| RN-033 | No se elimina una película o sala con funciones, un producto que está en un combo, un combo que está dentro de otro, ni un asiento vendido en una función programada. |
| RN-034 | Estadísticas: el dinero cuenta solo compras pagadas con tarjeta (ingreso neto = total − reembolsado); las unidades vendidas cuentan compras PAGADAS con tarjeta o con puntos. Las redenciones de producto o combo no tienen función y se omiten donde se necesita una. |

---

## 4. Entidades del dominio

| Entidad | Atributos principales | Responsabilidad |
|---|---|---|
| `Usuario` *(abstracta)* | nombre, correo, clave | Datos de acceso; define `getRol()`. |
| `Cliente` | tarjeta, cuentaPuntos, compras, notificaciones | Usuario que compra, acumula puntos y observa las funciones donde compró. |
| `Administrador` | — | Usuario que gestiona el cine. |
| `TarjetaVirtual` | numero, saldo, estado (`EstadoTarjeta`), movimientos | Medio de pago del cliente. |
| `MovimientoTarjeta` | tipo (`TipoMovimiento`), fecha, monto, descripcion | Historial de recargas, pagos y reembolsos. |
| `Pelicula` | codigo, titulo, genero, duracionMinutos, clasificacion, sinopsis, enCartelera | Contenido que se proyecta. |
| `Sala` | codigo, nombre, asientos | Espacio físico con sus asientos. |
| `Asiento` | fila, numero, tipo (`TipoAsiento`) | Ubicación física en una sala. |
| `Funcion` | codigo, pelicula, sala, fecha, hora, precioBase, estado (`EstadoFuncion`), asientosOcupados | Proyección; controla la ocupación de asientos. |
| `ItemVendible` *(interfaz)* | nombre, precio | Contrato común de lo que se vende. |
| `Entrada` *(abstracta)* | funcion, asiento | Derecho a un asiento de una función. |
| `EntradaGeneral` / `EntradaAccesible` | — | Entrada para asiento NORMAL / ACCESIBLE. |
| `Producto` | codigo, nombre, precio, descripcion | Artículo de confitería. |
| `Combo` | codigo, nombre, porcentajeDescuento, componentes (`ItemVendible`) | Agrupación de productos (u otros combos); su precio es la suma de los componentes menos su descuento. |
| `Promocion` | codigo, nombre, porcentajeDescuento, fechaInicio, fechaFin | Descuento con vigencia. |
| `Compra` | codigo, cliente, funcion, entradas, productos, combos, promocion, total, fecha, estado (`EstadoCompraState` → `EstadoCompra`), pagadaConPuntos, medioPago, puntosUsados, montoReembolsado | Transacción del cliente (pago o redención). Se arma con `Compra.Builder`. |
| `CuentaPuntos` | acumulaciones | Puntos del cliente. |
| `AcumulacionPuntos` | puntosIniciales, puntosDisponibles, fechaObtencion, fechaVencimiento, compraOrigen, anulada | Lote de puntos con vencimiento propio. |
| `ConfiguracionPuntos` | constantes | Regla de acumulación y tabla de redención. |
| `CinemaUQ` | nombre y listas de usuarios, películas, salas, funciones, productos, combos, promociones y compras | Raíz del modelo en memoria. |

Enumeraciones: `EstadoTarjeta` (ACTIVA, BLOQUEADA), `TipoMovimiento` (RECARGA, PAGO, REEMBOLSO), `TipoAsiento` (NORMAL, ACCESIBLE), `EstadoFuncion` (PROGRAMADA, CANCELADA, FINALIZADA), `EstadoCompra` (PENDIENTE, PAGADA, REEMBOLSADA).

Excepciones (`CinemaUQException` y subclases): `SaldoInsuficienteException`, `TarjetaInactivaException`, `AsientoNoDisponibleException`, `CancelacionNoPermitidaException`, `ReembolsoYaEjecutadoException`, `PuntosInsuficientesException`, `CredencialesInvalidasException`, `UsuarioYaRegistradoException`, `TipoAsientoInvalidoException`, `CompraInvalidaException`, `PromocionNoVigenteException`, `AccesoDenegadoException`, `FuncionNoDisponibleException`.

---

## 5. Relaciones y multiplicidades

| Relación | Tipo | Multiplicidad |
|---|---|---|
| `Cliente`, `Administrador` → `Usuario` | Herencia | — |
| `EntradaGeneral`, `EntradaAccesible` → `Entrada` | Herencia | — |
| `Entrada`, `Producto`, `Combo` → `ItemVendible` | Realización | — |
| `CinemaUQ` – `Usuario` | Agregación | 1 — 0..* |
| `CinemaUQ` – `Pelicula` / `Sala` / `Funcion` / `Producto` / `Combo` / `Promocion` / `Compra` | Agregación | 1 — 0..* |
| `Cliente` – `TarjetaVirtual` | Composición | 1 — 1 |
| `Cliente` – `CuentaPuntos` | Composición | 1 — 1 |
| `Cliente` – `Compra` | Asociación | 1 — 0..* |
| `TarjetaVirtual` – `MovimientoTarjeta` | Composición | 1 — 0..* |
| `Sala` – `Asiento` | Composición | 1 — 1..* |
| `Funcion` – `Pelicula` | Asociación | 0..* — 1 |
| `Funcion` – `Sala` | Asociación | 0..* — 1 |
| `Funcion` – `Asiento` (ocupados) | Asociación | 0..* — 0..* |
| `Entrada` – `Funcion` | Asociación | 0..* — 1 |
| `Entrada` – `Asiento` | Asociación | 0..* — 1 |
| `Compra` – `Funcion` | Asociación | 0..* — 0..1 (0 solo en redenciones de producto o combo) |
| `Compra` – `Entrada` | Composición | 1 — 0..* (1..* en compras con tarjeta) |
| `Compra` – `Producto` | Asociación | 0..* — 0..* |
| `Compra` – `Combo` | Asociación | 0..* — 0..* |
| `Compra` – `Promocion` | Asociación | 0..* — 0..1 |
| `Compra` – `MedioPago` (tarjeta de pago adaptada) | Asociación | 0..* — 0..1 |
| `Combo` – `ItemVendible` (componentes) | Agregación | 0..* — 1..* |
| `CuentaPuntos` – `AcumulacionPuntos` | Composición | 1 — 0..* |
| `AcumulacionPuntos` – `Compra` (origen) | Asociación | 0..1 — 0..1 |

---

## 6. Casos de uso principales

### CU-01 Iniciar sesión
**Actor:** Cliente / Administrador. **Flujo:** ingresa correo y clave → el sistema valida → abre la vista según el rol. **Excepción:** credenciales incorrectas → `CredencialesInvalidasException`.

### CU-02 Registrarse
**Actor:** Cliente. **Flujo:** ingresa nombre, correo y clave → el sistema crea el cliente con tarjeta ACTIVA (saldo 0) y cuenta de puntos. **Excepción:** correo existente → `UsuarioYaRegistradoException`.

### CU-03 Comprar entradas
**Actor:** Cliente.
1. Consulta la cartelera y elige una película.
2. Elige una función programada.
3. Selecciona uno o más asientos en el mapa de la sala.
4. Agrega productos y combos (opcional).
5. Aplica una promoción vigente (opcional).
6. Revisa el resumen y confirma.
7. El sistema verifica la tarjeta (activa, saldo), ocupa los asientos, descuenta el total, registra el movimiento PAGO, guarda la compra como PAGADA y acumula los puntos.

**Excepciones:** `TarjetaInactivaException`, `SaldoInsuficienteException`, `AsientoNoDisponibleException`.

### CU-04 Cancelar compra
**Actor:** Cliente.
1. Elige una compra PAGADA de su historial.
2. El sistema calcula el porcentaje según el tiempo que falta para la función.
3. El cliente confirma.
4. El sistema abona el reembolso a la tarjeta de pago, registra el movimiento REEMBOLSO, libera los asientos, marca la compra REEMBOLSADA y anula los puntos que generó si siguen sin usar.

**Excepciones:** menos de 2 h o compra pagada con puntos → `CancelacionNoPermitidaException`; compra ya reembolsada → `ReembolsoYaEjecutadoException`.

### CU-05 Redimir puntos
**Actor:** Cliente.
1. Elige el beneficio (entrada con función y asiento, producto o combo).
2. El sistema verifica que los puntos vigentes alcancen y los descuenta empezando por las acumulaciones más próximas a vencer.
3. Registra una compra PAGADA con `pagadaConPuntos = true`, sin movimiento de tarjeta y sin generar puntos.

**Excepciones:** `PuntosInsuficientesException`, `AsientoNoDisponibleException`.

### CU-06 Cancelar función
**Actor:** Administrador. **Flujo:** elige la función → el sistema la marca CANCELADA y reembolsa el 100 % de cada compra PAGADA con tarjeta (anulando sus puntos sin usar). Las redenciones con puntos de esa función no generan reembolso en dinero.

### CU-07 Recargar / activar / bloquear tarjeta
**Actor:** Administrador. **Flujo:** elige el cliente → ingresa el monto (> 0) → el sistema suma el saldo y registra el movimiento RECARGA. También puede cambiar el estado a ACTIVA o BLOQUEADA.

### CU-08 Gestionar catálogo
**Actor:** Administrador. **Flujo:** CRUD de películas, salas y asientos, funciones, productos, combos y promociones.

### CU-09 Consultar estadísticas y generar reportes
**Actor:** Administrador. **Flujo:** consulta los gráficos de JavaFX y exporta los reportes en TXT.

---

## 7. Patrones de diseño aplicables (GoF)

> Implementados en la fase 2; el detalle y la evidencia están en `docs/patrones_y_solid.md`.

| Patrón | Tipo | Dónde aplicaría |
|---|---|---|
| **Singleton** | Creacional | `CinemaUQ`: una única instancia con los datos en memoria, compartida por todos los controladores. |
| **Factory Method** | Creacional | Crear la `Entrada` correcta (`EntradaGeneral` o `EntradaAccesible`) según el tipo de asiento. |
| **Builder** | Creacional | Armar paso a paso una `Compra` (función, entradas, productos, combos, promoción) antes de confirmarla. |
| **Strategy** | Comportamiento | Política de reembolso: cancelación por cliente (100 %, 80 % o no permitida) frente a cancelación de la función por el cine (100 %). |
| **Observer** | Comportamiento | Interfaz propia `Observer` para avisar a las vistas cuando cambian la ocupación de una función, el saldo de una tarjeta o los puntos. |
| **Facade** | Estructural | Una clase de servicio que ofrece a los controladores operaciones simples (comprar, cancelar, redimir, recargar), ocultando la coordinación entre tarjeta, función, compra y puntos. |
| **Template Method** | Comportamiento | Reportes TXT con estructura fija (encabezado, cuerpo, pie) y cuerpo distinto en cada reporte. |

---

## 8. Servicios (lógica de negocio)

Paquete `service`, sin JavaFX. Los servicios delegan en `ProcesoCompraFacade`, las estrategias de reembolso y los estados de la compra; no duplican su lógica.

| Servicio | Responsabilidad |
|---|---|
| `AutenticacionService` | Registrar clientes (tarjeta ACTIVA con saldo 0 y cuenta de puntos) e iniciar sesión. |
| `CarteleraService` | Películas en cartelera, funciones disponibles por película y asientos disponibles. |
| `CompraService` | Comprar con tarjeta (con `Compra.Builder`, `EntradaFactory.segunAsiento` y la fachada) y redimir puntos por entrada, producto o combo. |
| `CancelacionService` | Cancelar compras (cliente dueño) y funciones (administrador), delegando en la fachada. |
| `PuntosService` | Consultar puntos vigentes y vencidos, y descontar puntos empezando por los más próximos a vencer. |
| `TarjetaService` | Saldo y movimientos; recarga, activación y bloqueo (administrador). |
| `AdministracionService` | CRUD del catálogo y consulta de clientes y compras (administrador). |
| `EstadisticaService` | Ventas por periodo, ocupación, películas y productos más vendidos, reembolsos y puntos redimidos. |
| `DatosIniciales` | Carga los datos de demostración pasando por los servicios. |

## 9. Programa de puntos

Todas las constantes están en `model/ConfiguracionPuntos.java`.

### Acumulación
- **1 punto por cada $100** pagados con la tarjeta virtual (`PESOS_POR_PUNTO = 100`). Se descartan las fracciones: una compra de $36.950 genera 369 puntos.
- Solo generan puntos las compras pagadas con tarjeta. Las redenciones no generan puntos.
- Cada compra crea una `AcumulacionPuntos` con su fecha de obtención y su fecha de vencimiento (**obtención + 1 año**, `ANIOS_VIGENCIA = 1`).
- Si la compra se reembolsa y su acumulación sigue sin usar, se anula. Si ya se usó, total o parcialmente, se conserva y el reembolso procede igual.

### Equivalencias de redención

| Beneficio | Puntos requeridos | Constante |
|---|---|---|
| 1 entrada (con asiento) | **1.000** | `PUNTOS_ENTRADA` |
| 1 producto | **400** | `PUNTOS_PRODUCTO` |
| 1 combo | **1.200** | `PUNTOS_COMBO` |

### Consumo
1. Se toman solo las acumulaciones vigentes (no vencidas, no anuladas, con saldo).
2. Se verifica que la suma alcance; si no, `PuntosInsuficientesException` y no se descuenta nada.
3. Se descuenta empezando por la acumulación con la fecha de vencimiento más próxima.

**Ejemplo:** un cliente tiene 300 puntos que vencen en 10 días, 900 que vencen en 6 meses y 500 ya vencidos. Su saldo canjeable es 1.200. Al redimir una entrada (1.000), se consumen los 300 primero y luego 700 de los 900. Quedan 200 vigentes; los 500 vencidos nunca se usan.
