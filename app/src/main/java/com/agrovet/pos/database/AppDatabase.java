package com.agrovet.pos.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.agrovet.pos.models.Abono;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.models.Proveedor;
import com.agrovet.pos.models.Venta;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Cliente.class, Producto.class, Proveedor.class, Venta.class, Movimiento.class, Abono.class}, version = 13, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ClienteDao clienteDao();
    public abstract ProductoDao productoDao();
    public abstract ProveedorDao proveedorDao();
    public abstract VentaDao ventaDao();
    public abstract MovimientoDao movimientoDao();
    public abstract AbonoDao abonoDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    
    // Actualizar datos de clientes
    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE cliente_new (cedula TEXT NOT NULL PRIMARY KEY, nombre TEXT NOT NULL, telefono TEXT NOT NULL, correo TEXT, direccion TEXT, fecha_creacion TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            database.execSQL("INSERT INTO cliente_new (cedula, nombre, telefono, correo, direccion, fecha_creacion) SELECT cedula, nombre, IFNULL(telefono, ''), correo, direccion, fecha_creacion FROM cliente");
            database.execSQL("DROP TABLE cliente");
            database.execSQL("ALTER TABLE cliente_new RENAME TO cliente");
        }
    };

    // Ajuste de version para sincronizar tablas
    static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Sin cambios en tablas, solo versionado
        }
    };

    // Sincronizacion de tablas y esquemas
    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Actualizar tabla cliente
            database.execSQL("CREATE TABLE IF NOT EXISTS `cliente_sync` (`cedula` TEXT NOT NULL, `nombre` TEXT NOT NULL, `telefono` TEXT, `correo` TEXT, `direccion` TEXT, `fecha_creacion` TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(`cedula`))");
            database.execSQL("INSERT OR IGNORE INTO `cliente_sync` SELECT * FROM `cliente` ");
            database.execSQL("DROP TABLE `cliente` ");
            database.execSQL("ALTER TABLE `cliente_sync` RENAME TO `cliente` ");

            // Actualizar tabla productos
            database.execSQL("CREATE TABLE IF NOT EXISTS `productos_sync` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `nombre` TEXT NOT NULL, `descripcion` TEXT, `categoria` TEXT NOT NULL, `cantidad` INTEGER DEFAULT 0, `presentacion` TEXT, `proveedor` TEXT, `precio_costo` INTEGER, `precio_venta` INTEGER)");
            database.execSQL("INSERT OR IGNORE INTO `productos_sync` SELECT * FROM `productos` ");
            database.execSQL("DROP TABLE `productos` ");
            database.execSQL("ALTER TABLE `productos_sync` RENAME TO `productos` ");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_productos_proveedor` ON `productos` (`proveedor`)");

            // Actualizar tabla proveedor
            database.execSQL("CREATE TABLE IF NOT EXISTS `proveedor_sync` (`telefono` TEXT NOT NULL, `nombre_empresa` TEXT NOT NULL, `nombre_proveedor` TEXT NOT NULL, `correo` TEXT, `estado` TEXT DEFAULT 'activo', `fecha_registro` TEXT DEFAULT CURRENT_TIMESTAMP, `producto` TEXT, PRIMARY KEY(`telefono`))");
            database.execSQL("INSERT OR IGNORE INTO `proveedor_sync` SELECT * FROM `proveedor` ");
            database.execSQL("DROP TABLE `proveedor` ");
            database.execSQL("ALTER TABLE `proveedor_sync` RENAME TO `proveedor` ");

            // Actualizar tabla ventas
            database.execSQL("CREATE TABLE IF NOT EXISTS `ventas_sync` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `numero_venta` INTEGER, `fecha_dia` TEXT NOT NULL, `fecha_hora` TEXT NOT NULL, `nombre_cliente` TEXT NOT NULL, `direccion_cliente` TEXT, `telefono_cliente` TEXT, `tipo_pago` TEXT NOT NULL, `cliente_cedula` TEXT, `subtotal` REAL NOT NULL DEFAULT 0.00, `descuento` REAL NOT NULL DEFAULT 0.00, `total` REAL NOT NULL DEFAULT 0.00, `dias_credito` INTEGER, `submetodo_banco` TEXT, `usuario_id` INTEGER DEFAULT 1, `estado` TEXT DEFAULT 'completada')");
            database.execSQL("INSERT OR IGNORE INTO `ventas_sync` SELECT * FROM `ventas` ");
            database.execSQL("DROP TABLE `ventas` ");
            database.execSQL("ALTER TABLE `ventas_sync` RENAME TO `ventas` ");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_ventas_cliente` ON `ventas` (`cliente_cedula`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_ventas_fecha` ON `ventas` (`fecha_dia`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_ventas_numero` ON `ventas` (`numero_venta`)");

            // Actualizar tabla abonos
            database.execSQL("CREATE TABLE IF NOT EXISTS `abonos_sync` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `credito_id` INTEGER NOT NULL, `venta_id` INTEGER NOT NULL, `cliente_cedula` TEXT NOT NULL, `monto` REAL NOT NULL, `fecha` TEXT NOT NULL, `metodo_pago` TEXT NOT NULL DEFAULT 'efectivo', `referencia` TEXT, `usuario_registra` TEXT, `observacion` TEXT, `fecha_registro` TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            database.execSQL("INSERT OR IGNORE INTO `abonos_sync` SELECT * FROM `abonos` ");
            database.execSQL("DROP TABLE `abonos` ");
            database.execSQL("ALTER TABLE `abonos_sync` RENAME TO `abonos` ");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_abonos_credito` ON `abonos` (`credito_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_abonos_fecha` ON `abonos` (`fecha`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_abonos_cliente_cedula` ON `abonos` (`cliente_cedula`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_abonos_venta_id` ON `abonos` (`venta_id`)");

            // Actualizar tabla reporte_caja
            database.execSQL("CREATE TABLE IF NOT EXISTS `reporte_caja_sync` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `ingresos` REAL DEFAULT 0.00, `razon_ingreso` TEXT, `fecha_ingreso` TEXT, `categoria` TEXT DEFAULT 'otros', `egresos` REAL DEFAULT 0.00, `razon_egreso` TEXT, `fecha_egreso` TEXT)");
            database.execSQL("INSERT OR IGNORE INTO `reporte_caja_sync` SELECT * FROM `reporte_caja` ");
            database.execSQL("DROP TABLE `reporte_caja` ");
            database.execSQL("ALTER TABLE `reporte_caja_sync` RENAME TO `reporte_caja` ");
            database.execSQL("CREATE INDEX IF NOT EXISTS `idx_reporte_caja_categoria` ON `reporte_caja` (`categoria`)");
        }
    };

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "Agrovet.db")
                            .createFromAsset("databases/Agrovet.db")
                            .addMigrations(MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
