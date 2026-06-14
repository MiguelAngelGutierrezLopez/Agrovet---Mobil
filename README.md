# Agrovet - Aplicacion Movil POS Hybrid

Sistema de Punto de Venta (POS) hibrido diseñado para la gestion de inventario, ventas y reportes financieros de Agrovet. La aplicacion permite el funcionamiento offline con sincronizacion bidireccional hacia servidores en la nube.

## Manual de Usuario Basico

### 1. Inicio (Dashboard)
Pantalla principal que muestra un resumen rapido del estado del negocio:
- Indicadores: Conteo total de clientes, productos, ventas del dia y saldo actual en caja.
- Acceso Rapido: Botones directos a las funciones principales (Ventas, Productos, Proveedores, etc.).
- Sincronizacion: Boton central para enviar datos locales a la web o recibir actualizaciones del servidor.

### 2. Gestion de Inventario (Productos)
Permite administrar el catalogo de productos.
- Visualizacion: Lista con indicadores de stock (Verde: OK, Amarillo: Medio, Rojo: Critico).
- Filtros: Busqueda por nombre, categoria o nivel de stock.
- Acciones: Crear, editar y eliminar productos localmente.

### 3. Ventas (POS)
Proceso de venta en dos pasos:
- Paso 1: Seleccion de productos del catalogo y gestion del carrito. Opcion de "Venta Especifica" para asignar un cliente.
- Paso 2: Seleccion de metodo de pago (Contado, Credito, Banco) y aplicacion de descuentos.
    - Credito: Requiere cliente especifico, permite definir anticipo y dias de credito.
    - Banco: Opciones para Nequi, Transaccion o Tarjeta.

### 4. Historial de Ventas
Consulta de transacciones pasadas.
- Filtros: Busqueda por rango de fechas (Inicio/Fin) o periodos predefinidos (Hoy, Semana, Mes).
- Detalle: Permite ver los productos especificos de cada venta y anular transacciones.

---

## Credenciales y Acceso

### Login de Aplicacion
Para acceder a la aplicacion, se deben utilizar las siguientes credenciales configuradas en el sistema:
- Usuario: admin
- Contraseña: AgroVet

### Servidores (Microservicios)
- Usuarios/Clientes: https://api-usuarios-production-bd11.up.railway.app/
- Inventario: https://api-inventario-production-3c43.up.railway.app/
- Ventas: https://api-ventas-production.up.railway.app/
- Reportes: https://api-reportes-production.up.railway.app/

---

## Configuracion y Dependencias

### Requisitos
- Android Studio Ladybug o superior.
- Java 17+.
- SDK de Android (Min: 24, Target: 34).

### Dependencias Principales
- Room Persistence: Base de datos local SQLite.
- Retrofit 2: Cliente HTTP para comunicacion con APIs REST.
- Gson: Serializacion y deserializacion de JSON.
- Lifecycle (ViewModel & LiveData): Implementacion del patron MVVM.
- Material Design: Componentes visuales de Google.

### Archivos de Configuracion Clave
- app/src/main/java/com/agrovet/pos/utils/Constants.java: Contiene las URLs de los servidores.
- app/src/main/java/com/agrovet/pos/database/AppDatabase.java: Configuracion de Room y migraciones.

---

## 🔄 Flujo de Sincronización
La aplicación utiliza un modelo "Local-First". Todos los registros se guardan primero en la base de datos local y se marcan con una bandera `is_synced = 0`. El `SyncManager` se encarga de:
1. **Push**: Enviar registros locales nuevos al servidor.
2. **Pull**: Descargar actualizaciones de precios, stock e historial de ventas desde la nube.
