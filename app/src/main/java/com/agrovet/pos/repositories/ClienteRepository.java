package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.ClienteDao;
import com.agrovet.pos.models.Cliente;
import java.util.List;

public class ClienteRepository {

    private final ClienteDao clienteDao;

    public ClienteRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        clienteDao = db.clienteDao();
    }

    public LiveData<List<Cliente>> getClientes() {
        return clienteDao.getAllClientes();
    }

    public void insert(Cliente cliente) {
        AppDatabase.databaseWriteExecutor.execute(() -> clienteDao.insert(cliente));
    }

    public void update(Cliente cliente) {
        AppDatabase.databaseWriteExecutor.execute(() -> clienteDao.update(cliente));
    }

    public void delete(Cliente cliente) {
        AppDatabase.databaseWriteExecutor.execute(() -> clienteDao.delete(cliente));
    }
    
    public void deleteById(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> clienteDao.deleteById(id));
    }
}
