package com.agrovet.pos.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.models.CartItem;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.repositories.VentaRepository;
import java.util.List;

public class VentaViewModel extends AndroidViewModel {
    private final VentaRepository repository;

    public VentaViewModel(@NonNull Application application) {
        super(application);
        repository = new VentaRepository(application);
    }

    public LiveData<List<Venta>> getAllVentas() {
        return repository.getAllVentas();
    }

    public void deleteVenta(int id) {
        repository.deleteById((long)id);
    }
    
    public void addVenta(Venta venta, List<CartItem> items) {
        repository.insert(venta, items);
    }

    public void getItemsByVenta(int ventaId, VentaRepository.OnItemsLoadedCallback callback) {
        repository.getItemsByVenta(ventaId, callback);
    }
}
