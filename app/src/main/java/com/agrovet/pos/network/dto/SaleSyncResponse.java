package com.agrovet.pos.network.dto;

import com.agrovet.pos.models.Venta;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SaleSyncResponse {
    @SerializedName("success")
    public boolean success;
    
    @SerializedName("ventas")
    public List<Venta> ventas;

    @SerializedName("total")
    public Integer total;

    public List<Venta> getVentas() { return ventas; }
}
