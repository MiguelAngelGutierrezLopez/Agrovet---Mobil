package com.agrovet.pos.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.agrovet.pos.models.Venta;
import java.util.List;

@Dao
public interface VentaDao {
    @Query("SELECT * FROM ventas ORDER BY id DESC")
    LiveData<List<Venta>> getAllVentas();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Venta venta);

    @Update
    void update(Venta venta);

    @Delete
    void delete(Venta venta);
    
    @Query("SELECT * FROM ventas WHERE is_synced = 0")
    List<Venta> getUnsyncedVentas();

    @Query("SELECT * FROM ventas WHERE server_id = :serverId LIMIT 1")
    Venta findByServerId(Integer serverId);

    @Query("SELECT * FROM ventas ORDER BY id DESC")
    List<Venta> getAllVentasList();

    @Query("SELECT MAX(server_id) FROM ventas")
    Integer getMaxServerId();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOrIgnore(Venta venta);

    @Query("SELECT * FROM ventas WHERE id = :id")
    Venta getVentaById(long id);

    @Query("DELETE FROM ventas WHERE id = :id")
    void deleteById(long id);
}
