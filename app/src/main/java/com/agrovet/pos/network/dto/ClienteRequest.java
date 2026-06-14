package com.agrovet.pos.network.dto;

import com.google.gson.annotations.SerializedName;

public class ClienteRequest {
    @SerializedName("cedula")
    private String cedula;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("correo")
    private String correo;
    
    @SerializedName("direccion")
    private String direccion;

    // Getters and Setters
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
