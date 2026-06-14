package com.agrovet.pos.network.dto;

import com.agrovet.pos.models.Producto;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductSyncResponse {
    @SerializedName("success")
    public boolean success;
    
    @SerializedName("productos")
    public List<Producto> productos;
    
    @SerializedName("total")
    public Integer total;

    public List<Producto> getProductos() { return productos; }
}
