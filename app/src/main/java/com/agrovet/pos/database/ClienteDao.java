package com.agrovet.pos.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.agrovet.pos.models.Cliente;
import java.util.List;

@Dao
public interface ClienteDao {
    @Query("SELECT * FROM cliente")
    LiveData<List<Cliente>> getAllClientes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Cliente cliente);

    @Update
    void update(Cliente cliente);

    @Delete
    void delete(Cliente cliente);
    
    @Query("SELECT * FROM cliente WHERE is_synced = 0")
    List<Cliente> getUnsyncedClientes();

    @Query("SELECT * FROM cliente WHERE cedula = :cedula LIMIT 1")
    Cliente findByCedula(String cedula);

    @Query("SELECT MAX(cedula) FROM cliente") // Clientes use Cedula as ID, but if there's a serial sync ID we'd use that.
    String getMaxId();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOrIgnore(Cliente cliente);

    @Query("DELETE FROM cliente WHERE cedula = :id")
    void deleteById(String id);
}
