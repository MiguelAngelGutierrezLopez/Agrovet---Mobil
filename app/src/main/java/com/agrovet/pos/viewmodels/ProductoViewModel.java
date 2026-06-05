package com.agrovet.pos.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.repositories.ProductoRepository;
import java.util.List;

public class ProductoViewModel extends AndroidViewModel {
    private final ProductoRepository repository;

    public ProductoViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ProductoRepository(application);
    }

    public LiveData<List<Producto>> getProductos() {
        return repository.getProductos();
    }

    public void createProducto(Producto producto) {
        repository.insert(producto);
    }

    public void updateProducto(Producto producto) {
        repository.update(producto);
    }

    public void deleteProducto(int id) {
        repository.deleteById(id);
    }
}
