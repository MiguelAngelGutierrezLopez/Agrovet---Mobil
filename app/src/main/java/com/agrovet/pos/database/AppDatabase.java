package com.agrovet.pos.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.models.Proveedor;
import com.agrovet.pos.models.Venta;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Cliente.class, Producto.class, Proveedor.class, Venta.class, Movimiento.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ClienteDao clienteDao();
    public abstract ProductoDao productoDao();
    public abstract ProveedorDao proveedorDao();
    public abstract VentaDao ventaDao();
    public abstract MovimientoDao movimientoDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "Agrovet.db")
                            .createFromAsset("databases/Agrovet.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
