package com.agrovet.pos.network;

import com.agrovet.pos.models.Venta;
import com.agrovet.pos.network.dto.GenericResponse;
import com.agrovet.pos.network.dto.SaleSyncResponse;
import com.agrovet.pos.network.dto.VentaRequest;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface SalesApiService {
    @POST("api/ventas/nueva")
    Call<GenericResponse> createVenta(@Body VentaRequest request);

    @GET("api/ventas/ultimo-ticket")
    Call<Map<String, Object>> getUltimoTicket();

    @GET("api/historial-ventas")
    Call<Map<String, Object>> getHistorialVentas();

    @GET("api/historial-ventas/recientes")
    Call<Map<String, Object>> getVentasRecientes();

    @GET("api/ventas/sync/")
    Call<SaleSyncResponse> syncVentas(@Query("last_sync_id") Integer lastSyncId);
}
