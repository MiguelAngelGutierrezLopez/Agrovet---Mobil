package com.agrovet.pos.network;

import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.network.dto.GenericResponse;
import com.agrovet.pos.network.dto.MovementSyncResponse;
import com.agrovet.pos.network.dto.MovimientoRequest;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ReportApiService {
    @GET("api/reporte-caja/movimientos")
    Call<Map<String, Object>> getMovimientos();

    @POST("api/reporte-caja/movimiento")
    Call<GenericResponse> createMovimiento(@Body MovimientoRequest request);

    @GET("api/reporte-caja/resumen")
    Call<Map<String, Object>> getResumenCaja();

    @GET("api/reporte-caja/estadisticas")
    Call<Map<String, Object>> getEstadisticas();

    @GET("api/reporte-caja/movimientos")
    Call<MovementSyncResponse> syncMovimientos(@Query("last_sync_id") Integer lastSyncId);
}
