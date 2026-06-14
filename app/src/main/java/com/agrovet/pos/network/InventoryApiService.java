package com.agrovet.pos.network;

import com.agrovet.pos.models.Producto;
import com.agrovet.pos.models.Proveedor;
import com.agrovet.pos.network.dto.GenericResponse;
import com.agrovet.pos.network.dto.ProductSyncResponse;
import com.agrovet.pos.network.dto.ProductoRequest;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface InventoryApiService {
    @GET("api/productos")
    Call<Map<String, Object>> getProductos(@Query("per_page") int perPage);

    @GET("api/productos/{id}")
    Call<Map<String, Object>> getProducto(@Path("id") int id);

    @POST("api/productos/")
    Call<GenericResponse> createProducto(@Body ProductoRequest request);

    @PUT("api/productos/{id}")
    Call<Map<String, Object>> updateProducto(@Path("id") int id, @Body Map<String, Object> productoData);

    @DELETE("api/productos/{id}")
    Call<Map<String, Object>> deleteProducto(@Path("id") int id);

    @GET("api/productos/proveedores")
    Call<Map<String, Object>> getProveedores();

    @GET("api/productos/categorias")
    Call<Map<String, Object>> getCategorias();

    @GET("api/productos")
    Call<ProductSyncResponse> getSyncProductos(@Query("last_sync_id") Integer lastSyncId);
}
