package com.agrovet.pos.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.repositories.ClienteRepository;
import java.util.List;

public class ClienteViewModel extends AndroidViewModel {
    private final ClienteRepository repository;

    public ClienteViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ClienteRepository(application);
    }

    public LiveData<List<Cliente>> getClientes() {
        return repository.getClientes();
    }

    public void createCliente(Cliente cliente) {
        repository.insert(cliente);
    }

    public void updateCliente(Cliente cliente) {
        repository.update(cliente);
    }

    public void deleteCliente(String id) {
        repository.deleteById(id);
    }
}
