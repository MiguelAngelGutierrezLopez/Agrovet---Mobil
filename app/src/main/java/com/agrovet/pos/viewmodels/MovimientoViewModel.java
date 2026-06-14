package com.agrovet.pos.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.repositories.MovimientoRepository;
import java.util.List;

public class MovimientoViewModel extends AndroidViewModel {
    private final MovimientoRepository repository;

    public MovimientoViewModel(@NonNull Application application) {
        super(application);
        repository = new MovimientoRepository(application);
    }

    public LiveData<List<Movimiento>> getAllMovimientos() {
        return repository.getAllMovimientos();
    }

    public void addMovimiento(Movimiento movimiento) {
        repository.insert(movimiento);
    }

    public void deleteMovimiento(Movimiento movimiento) {
        repository.delete(movimiento);
    }

    public void updateMovimiento(Movimiento movimiento) {
        repository.update(movimiento);
    }
}
