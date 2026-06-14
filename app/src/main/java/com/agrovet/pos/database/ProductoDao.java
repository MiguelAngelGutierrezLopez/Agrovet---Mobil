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
    
    @Query("SELECT * FROM productos WHERE is_synced = 0")
    List<Producto> getUnsyncedProductos();

    @Query("SELECT * FROM productos WHERE server_id = :serverId LIMIT 1")
    Producto findByServerId(Integer serverId);

    @Query("SELECT MAX(server_id) FROM productos")
    Integer getMaxServerId();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOrIgnore(Producto producto);

    @Query("DELETE FROM productos WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    Producto getProductoById(int id);
}
