package com.agrovet.pos.network;

import com.agrovet.pos.models.Cliente;
import com.agrovet.pos.models.Proveedor;
import com.agrovet.pos.network.dto.ClientSyncResponse;
import com.agrovet.pos.network.dto.ClienteRequest;
import com.agrovet.pos.network.dto.GenericResponse;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApiService {
    // Clientes
    @GET("api/clientes")
    Call<Map<String, Object>> getClientes(@Query("per_page") int perPage);

    @GET("api/cliente/{cedula}")
    Call<Map<String, Object>> getCliente(@Path("cedula") String cedula);

    @POST("api/clientes-proveedores/cliente")
    Call<GenericResponse> createCliente(@Body ClienteRequest request);

    @PUT("api/clientes/{id}")
    Call<Map<String, Object>> updateCliente(@Path("id") int id, @Body Map<String, Object> clienteData);

    @DELETE("api/cliente/{cedula}")
    Call<Map<String, Object>> deleteCliente(@Path("cedula") String cedula);

    // Proveedores
    @GET("api/proveedores")
    Call<Map<String, Object>> getProveedores();

    @GET("api/proveedor/{telefono}")
    Call<Map<String, Object>> getProveedor(@Path("telefono") String telefono);

    @POST("api/proveedor/completo")
    Call<GenericResponse> createProveedorCompleto(@Body Proveedor proveedor);

    @PUT("api/proveedor/{telefono}")
    Call<GenericResponse> updateProveedorCompleto(@Path("telefono") String telefono, @Body Proveedor proveedor);

    @DELETE("api/proveedor/{telefono}")
    Call<Map<String, Object>> deleteProveedor(@Path("telefono") String telefono);

    @GET("api/clientes")
    Call<ClientSyncResponse> getClientesSync();

    @GET("api/clientes-proveedores/clientes")
    Call<ClientSyncResponse> getSyncClientes(@Query("last_sync_id") Integer lastSyncId);
}
