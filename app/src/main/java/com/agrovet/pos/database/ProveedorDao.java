package com.agrovet.pos.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.agrovet.pos.models.Proveedor;
import java.util.List;

@Dao
public interface ProveedorDao {
    @Query("SELECT * FROM Proveedores")
    LiveData<List<Proveedor>> getAllProveedores();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Proveedor proveedor);

    @Update
    void update(Proveedor proveedor);

    @Delete
    void delete(Proveedor proveedor);
    
    @Query("DELETE FROM Proveedores WHERE Credencial = :id")
    void deleteById(long id);
}
