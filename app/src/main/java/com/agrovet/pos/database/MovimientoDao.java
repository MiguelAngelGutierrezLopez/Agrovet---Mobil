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

    @Delete
    void delete(Movimiento movimiento);
}
