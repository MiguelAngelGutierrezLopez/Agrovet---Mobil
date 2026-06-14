package com.agrovet.pos.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.agrovet.pos.models.Producto;
import java.util.List;

@Dao
public interface ProductoDao {
    @Query("SELECT * FROM productos")
    LiveData<List<Producto>> getAllProductos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Producto producto);

    @Update
    void update(Producto producto);

    @Delete
    void delete(Producto producto);
    
    @Query("DELETE FROM productos WHERE Codigo = :id")
    void deleteById(long id);
}
