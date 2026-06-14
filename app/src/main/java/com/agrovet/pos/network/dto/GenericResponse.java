package com.agrovet.pos.network.dto;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private Integer id; // Captura ID directo si viene como {"id": 123}

    @SerializedName("venta_id")
    private Integer ventaId;

    @SerializedName("ticket_numero")
    private Integer ticketNumero;

    @SerializedName("producto_id")
    private Integer productoId;

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("data")
    private DataPayload data;

    public static class DataPayload {
        @SerializedName("id")
        private Integer id;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Integer getId() { return id; }
    public Integer getVentaId() { return ventaId; }
    public Integer getTicketNumero() { return ticketNumero; }
    public Integer getProductoId() { return productoId; }
    public String getCedula() { return cedula; }
    public DataPayload getData() { return data; }
}
