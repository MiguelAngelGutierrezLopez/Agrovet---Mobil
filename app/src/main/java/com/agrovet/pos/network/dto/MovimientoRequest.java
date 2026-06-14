package com.agrovet.pos.network.dto;

import com.google.gson.annotations.SerializedName;

public class MovimientoRequest {
    @SerializedName("tipo")
    private String tipo;
    
    @SerializedName("monto")
    private double monto;
    
    @SerializedName("razon")
    private String razon;
    
    @SerializedName("categoria")
    private String categoria;

    // Getters and Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getRazon() { return razon; }
    public void setRazon(String razon) { this.razon = razon; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
