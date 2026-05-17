package com.agrovet.pos.network;

import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.models.Producto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("clientes")
    Call<List<Cliente>> getClientes();

    @POST("clientes")
    Call<Cliente> createCliente(@Body Cliente cliente);

    @PUT("clientes/{id}")
    Call<Cliente> updateCliente(@Path("id") String id, @Body Cliente cliente);

    @DELETE("clientes/{id}")
    Call<Void> deleteCliente(@Path("id") String id);

    @GET("productos")
    Call<List<Producto>> getProductos();

    @POST("productos")
    Call<Producto> createProducto(@Body Producto producto);

    @PUT("productos/{id}")
    Call<Producto> updateProducto(@Path("id") String id, @Body Producto producto);

    @DELETE("productos/{id}")
    Call<Void> deleteProducto(@Path("id") String id);
}
