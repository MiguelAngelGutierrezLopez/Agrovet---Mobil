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
    @GET("clientes")
    Call<Map<String, Object>> getClientes(@Query("per_page") int perPage);

    @GET("cliente/{cedula}")
    Call<Map<String, Object>> getCliente(@Path("cedula") String cedula);

    @POST("clientes-proveedores/cliente")
    Call<GenericResponse> createCliente(@Body ClienteRequest request);

    @PUT("clientes/{id}")
    Call<Map<String, Object>> updateCliente(@Path("id") int id, @Body Map<String, Object> clienteData);

    @DELETE("cliente/{cedula}")
    Call<Map<String, Object>> deleteCliente(@Path("cedula") String cedula);

    // Proveedores
    @GET("proveedores")
    Call<Map<String, Object>> getProveedores();

    @GET("proveedor/{telefono}")
    Call<Map<String, Object>> getProveedor(@Path("telefono") String telefono);

    @POST("proveedor/completo")
    Call<Map<String, Object>> createProveedor(@Body Proveedor proveedor);

    @PUT("proveedor/{telefono}")
    Call<Map<String, Object>> updateProveedor(@Path("telefono") String telefono, @Body Proveedor proveedor);

    @DELETE("proveedor/{telefono}")
    Call<Map<String, Object>> deleteProveedor(@Path("telefono") String telefono);

    @GET("clientes-proveedores/clientes")
    Call<ClientSyncResponse> getSyncClientes(@Query("last_sync_id") Integer lastSyncId);
}
