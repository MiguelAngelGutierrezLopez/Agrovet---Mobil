package com.agrovet.pos.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.agrovet.pos.models.Proveedor;
import java.util.List;

@Dao
public interface ProveedorDao {
    @Query("SELECT * FROM proveedor")
    LiveData<List<Proveedor>> getAllProveedores();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Proveedor proveedor);

    @Update
    void update(Proveedor proveedor);

    @Delete
    void delete(Proveedor proveedor);
    
    @Query("DELETE FROM proveedor WHERE telefono = :id")
    void deleteById(String id);

    @Query("SELECT * FROM proveedor WHERE is_synced = 0")
    List<Proveedor> getUnsyncedProveedores();
}
