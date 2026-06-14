package com.agrovet.pos.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.agrovet.pos.models.Movimiento;
import java.util.List;

@Dao
public interface MovimientoDao {
    @Query("SELECT * FROM reporte_caja ORDER BY id DESC")
    LiveData<List<Movimiento>> getAllMovimientos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movimiento movimiento);

    @Update
    void update(Movimiento movimiento);

    @Delete
    void delete(Movimiento movimiento);

    @Query("SELECT * FROM reporte_caja WHERE is_synced = 0")
    List<Movimiento> getUnsyncedMovimientos();

    @Query("SELECT MAX(server_id) FROM reporte_caja")
    Integer getMaxServerId();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOrIgnore(Movimiento movimiento);

    @Query("SELECT * FROM reporte_caja WHERE server_id = :serverId LIMIT 1")
    Movimiento findByServerId(Integer serverId);
}
