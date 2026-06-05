package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.MovimientoDao;
import com.agrovet.pos.models.Movimiento;
import java.util.List;

public class MovimientoRepository {
    private final MovimientoDao movimientoDao;

    public MovimientoRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        movimientoDao = db.movimientoDao();
    }

    public LiveData<List<Movimiento>> getAllMovimientos() {
        return movimientoDao.getAllMovimientos();
    }

    public void insert(Movimiento movimiento) {
        AppDatabase.databaseWriteExecutor.execute(() -> movimientoDao.insert(movimiento));
    }
}
