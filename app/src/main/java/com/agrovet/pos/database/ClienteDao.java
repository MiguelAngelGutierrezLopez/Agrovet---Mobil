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
    
    @Query("DELETE FROM cliente WHERE cedula = :id")
    void deleteById(String id);
}
