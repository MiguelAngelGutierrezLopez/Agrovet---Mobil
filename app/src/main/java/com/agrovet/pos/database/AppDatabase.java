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

@Database(entities = {Cliente.class, Producto.class, Proveedor.class, Venta.class, Movimiento.class, Abono.class}, version = 12, exportSchema = false)
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

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "Agrovet.db")
                            .createFromAsset("databases/Agrovet.db")
                            .addMigrations(MIGRATION_10_11, MIGRATION_11_12)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
