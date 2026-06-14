package com.agrovet.pos.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.agrovet.pos.AgrovetApplication;
import com.agrovet.pos.database.AppDatabase;
import com.agrovet.pos.database.ClienteDao;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.network.RetrofitClient;
import com.agrovet.pos.network.UserApiService;
import com.agrovet.pos.utils.AppLogger;
import com.agrovet.pos.utils.DebugLog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteRepository {
    private final ClienteDao clienteDao;
    private final UserApiService apiService;

    public ClienteRepository(Application application) {
        AppDatabase db = ((AgrovetApplication) application).getDatabase();
        clienteDao = db.clienteDao();
        apiService = RetrofitClient.getUsuariosClient().create(UserApiService.class);
    }

    public LiveData<List<Cliente>> getClientes() {
        return clienteDao.getAllClientes();
    }

    public void insert(Cliente cliente) {
        cliente.setSynced(false);
        DebugLog.sql("cliente", "INSERT (Local)", cliente.getCedula());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            clienteDao.insert(cliente);
            // Ya no sincronizamos inmediatamente
        });
    }

    public void refreshClientes() {
        DebugLog.api("GET api/clientes", "Iniciando (Carga Total)", null);
        // Pedimos 1000 para traer todos de una vez y evitar paginación de 10
        apiService.getClientes(1000).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = Boolean.TRUE.equals(response.body().get("success"));
                    if (success) {
                        List<Map<String, Object>> clientesRaw = (List<Map<String, Object>>) response.body().get("clientes");
                        int count = (clientesRaw != null) ? clientesRaw.size() : 0;
                        DebugLog.api("GET api/clientes", "EXITO", "Recibidos: " + count);
                        
                        if (clientesRaw != null) {
                            AppDatabase.databaseWriteExecutor.execute(() -> {
                                for (Map<String, Object> map : clientesRaw) {
                                    Cliente c = new Cliente();
                                    c.setCedula(String.valueOf(map.get("cedula") != null ? map.get("cedula") : map.get("documento")));
                                    c.setNombre(String.valueOf(map.get("nombre")));
                                    c.setTelefono(map.get("telefono") != null ? String.valueOf(map.get("telefono")) : "");
                                    c.setCorreo(map.get("email") != null ? String.valueOf(map.get("email")) : (map.get("correo") != null ? String.valueOf(map.get("correo")) : ""));
                                    c.setDireccion(map.get("direccion") != null ? String.valueOf(map.get("direccion")) : "");
                                    c.setFechaCreacion(map.get("fecha_creacion") != null ? String.valueOf(map.get("fecha_creacion")) : "");
                                    c.setSynced(true);
                                    clienteDao.insert(c);
                                }
                            });
                        }
                    } else {
                        DebugLog.api("GET api/clientes", "FALLO_API", response.body().get("message"));
                    }
                } else {
                    DebugLog.api("GET api/clientes", "ERROR_HTTP " + response.code(), null);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                DebugLog.error("Refresh Clientes", t);
                AppLogger.e("Error al refrescar clientes", t);
            }
        });
    }

    public void update(Cliente cliente) {
        cliente.setSynced(false);
        AppDatabase.databaseWriteExecutor.execute(() -> clienteDao.update(cliente));
    }

    public void deleteById(String id) {
        AppDatabase.databaseWriteExecutor.execute(() -> clienteDao.deleteById(id));
    }
}
