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
    void insert(Venta venta);

    @Delete
    void delete(Venta venta);
    
    @Query("DELETE FROM ventas WHERE id = :id")
    void deleteById(long id);
}
