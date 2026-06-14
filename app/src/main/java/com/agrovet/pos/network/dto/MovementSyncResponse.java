package com.agrovet.pos.network.dto;

import com.agrovet.pos.models.Movimiento;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovementSyncResponse {
    @SerializedName("success")
    public boolean success;
    
    @SerializedName("data")
    public MovementData data;

    @SerializedName("total")
    public Integer total;

    public static class MovementData {
        public MovementData() {}

        @SerializedName("movimientos")
        public List<Movimiento> movimientos;

        public List<Movimiento> getMovimientos() { return movimientos; }
    }

    public MovementData getData() { return data; }
}
