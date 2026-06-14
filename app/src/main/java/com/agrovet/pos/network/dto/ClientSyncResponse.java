package com.agrovet.pos.network.dto;

import com.agrovet.pos.models.Cliente;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClientSyncResponse {
    @SerializedName("success")
    public boolean success;
    
    @SerializedName("clientes")
    public List<Cliente> clientes;

    @SerializedName("total")
    public Integer total;

    public List<Cliente> getClientes() { return clientes; }
}
