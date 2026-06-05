package com.agrovet.pos.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.models.Proveedor;
import com.agrovet.pos.repositories.ProveedorRepository;
import java.util.List;

public class ProveedorViewModel extends AndroidViewModel {
    private final ProveedorRepository repository;

    public ProveedorViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ProveedorRepository(application);
    }

    public LiveData<List<Proveedor>> getProveedores() {
        return repository.getProveedores();
    }

    public void createProveedor(Proveedor proveedor) {
        repository.insert(proveedor);
    }

    public void updateProveedor(Proveedor proveedor) {
        repository.update(proveedor);
    }

    public void deleteProveedor(String id) {
        repository.deleteById(id);
    }
}
