package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.MovimientoDao;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.network.ReportApiService;
import com.agrovet.pos.network.RetrofitClient;
import com.agrovet.pos.utils.DebugLog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovimientoRepository {
    private final MovimientoDao movimientoDao;
    private final ReportApiService apiService;

    public MovimientoRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        movimientoDao = db.movimientoDao();
        apiService = RetrofitClient.getReportesClient().create(ReportApiService.class);
    }

    public LiveData<List<Movimiento>> getAllMovimientos() {
        return movimientoDao.getAllMovimientos();
    }

    public void insert(Movimiento movimiento) {
        movimiento.setSynced(false);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            movimientoDao.insert(movimiento);
            // Sincronización diferida
        });
    }

    public void delete(Movimiento movimiento) {
        AppDatabase.databaseWriteExecutor.execute(() -> movimientoDao.delete(movimiento));
    }

    public void update(Movimiento movimiento) {
        movimiento.setSynced(false);
        AppDatabase.databaseWriteExecutor.execute(() -> movimientoDao.update(movimiento));
    }
}
