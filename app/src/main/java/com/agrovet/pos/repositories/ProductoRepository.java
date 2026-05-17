package com.agrovet.pos.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.network.ApiClient;
import com.agrovet.pos.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoRepository {

    private final ApiService apiService;

    public ProductoRepository(android.app.Application application) {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<List<Producto>> getProductos() {
        MutableLiveData<List<Producto>> data = new MutableLiveData<>();
        apiService.getProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public void insert(Producto producto) {
        apiService.createProducto(producto).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {}
            @Override
            public void onFailure(Call<Producto> call, Throwable t) {}
        });
    }

    public void update(Producto producto) {
        apiService.updateProducto(String.valueOf(producto.getCodigo()), producto).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {}
            @Override
            public void onFailure(Call<Producto> call, Throwable t) {}
        });
    }

    public void deleteById(long id) {
        apiService.deleteProducto(String.valueOf(id)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}
