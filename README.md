# Agrovet - Aplicación Móvil POS Hybrid

Sistema de Punto de Venta (POS) híbrido diseñado para la gestión de inventario, ventas y reportes financieros de Agrovet. La aplicación permite el funcionamiento offline con sincronización bidireccional hacia servidores en la nube.

## 📱 Manual de Usuario Básico

### 1. Inicio (Dashboard)
Pantalla principal que muestra un resumen rápido del estado del negocio:
- **Indicadores**: Conteo total de clientes, productos, ventas del día y saldo actual en caja.
- **Acceso Rápido**: Botones directos a las funciones principales (Ventas, Productos, Proveedores, etc.).
- **Sincronización**: Botón central para enviar datos locales a la web o recibir actualizaciones del servidor.

### 2. Gestión de Inventario (Productos)
Permite administrar el catálogo de productos.
- **Visualización**: Lista con indicadores de stock (Verde: OK, Amarillo: Medio, Rojo: Crítico).
- **Filtros**: Búsqueda por nombre, categoría o nivel de stock.
- **Acciones**: Crear, editar y eliminar productos localmente.

### 3. Ventas (POS)
Proceso de venta en dos pasos:
- **Paso 1**: Selección de productos del catálogo y gestión del carrito. Opción de "Venta Específica" para asignar un cliente.
- **Paso 2**: Selección de método de pago (Contado, Crédito, Banco) y aplicación de descuentos. 
    - *Crédito*: Requiere cliente específico, permite definir anticipo y días de crédito.
    - *Banco*: Opciones para Nequi, Transacción o Tarjeta.

### 4. Historial de Ventas
Consulta de transacciones pasadas.
- **Filtros**: Búsqueda por rango de fechas (Inicio/Fin) o periodos predefinidos (Hoy, Semana, Mes).
- **Detalle**: Permite ver los productos específicos de cada venta y anular transacciones.

### 5. Reporte de Caja
Control de flujo de efectivo manual.
- Permite registrar ingresos y egresos extraordinarios que no provienen directamente de ventas (ej: gastos de local, abonos manuales).

---

## 🔐 Credenciales y Acceso

### Login de Aplicación
Actualmente, la aplicación utiliza un flujo simplificado. Las credenciales de acceso a los servicios web están preconfiguradas en el código para asegurar la conectividad con los microservicios en Railway.

### Servidores (Microservicios)
- **Usuarios/Clientes**: `https://api-usuarios-production-bd11.up.railway.app/`
- **Inventario**: `https://api-inventario-production-3c43.up.railway.app/`
- **Ventas**: `https://api-ventas-production.up.railway.app/`
- **Reportes**: `https://api-reportes-production.up.railway.app/`

---

## 🛠️ Configuración y Dependencias

### Requisitos
- Android Studio Ladybug o superior.
- Java 17+.
- SDK de Android (Min: 24, Target: 34).

### Dependencias Principales
- **Room Persistence**: Base de datos local SQLite.
- **Retrofit 2**: Cliente HTTP para comunicación con APIs REST.
- **Gson**: Serialización y deserialización de JSON.
- **Lifecycle (ViewModel & LiveData)**: Implementación del patrón MVVM.
- **Material Design**: Componentes visuales de Google.
- **SwipeRefreshLayout**: Interacción de actualización de listas.

### Archivos de Configuración Clave
- `app/src/main/java/com/agrovet/pos/utils/Constants.java`: Contiene las URLs de los servidores.
- `app/src/main/java/com/agrovet/pos/database/AppDatabase.java`: Configuración de Room y migraciones.

---

## 🔄 Flujo de Sincronización
La aplicación utiliza un modelo "Local-First". Todos los registros se guardan primero en la base de datos local y se marcan con una bandera `is_synced = 0`. El `SyncManager` se encarga de:
1. **Push**: Enviar registros locales nuevos al servidor.
2. **Pull**: Descargar actualizaciones de precios, stock e historial de ventas desde la nube.
