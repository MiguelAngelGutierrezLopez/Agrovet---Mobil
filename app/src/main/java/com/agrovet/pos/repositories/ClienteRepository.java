package com.agrovet.pos.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.network.ApiClient;
import com.agrovet.pos.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteRepository {

    private final ApiService apiService;

    public ClienteRepository(android.app.Application application) {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<List<Cliente>> getClientes() {
        MutableLiveData<List<Cliente>> data = new MutableLiveData<>();
        apiService.getClientes().enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public void insert(Cliente cliente) {
        apiService.createCliente(cliente).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {}
            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {}
        });
    }

    public void update(Cliente cliente) {
        apiService.updateCliente(cliente.getId(), cliente).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {}
            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {}
        });
    }

    public void deleteById(long id) {
        apiService.deleteCliente(String.valueOf(id)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}
