package com.agrovet.pos.network.dto;

import com.google.gson.annotations.SerializedName;

public class ProductoRequest {
    @SerializedName("id")
    private Integer id; // Null for new, set for update if needed

    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("descripcion")
    private String descripcion;
    
    @SerializedName("categoria")
    private String categoria;
    
    @SerializedName("cantidad")
    private int cantidad;
    
    @SerializedName("presentacion")
    private String presentacion;
    
    @SerializedName("precio_venta")
    private double precioVenta;
    
    @SerializedName("precio_costo")
    private double precioCosto;
    
    @SerializedName("proveedor")
    private String proveedor;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double precioCosto) { this.precioCosto = precioCosto; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
}
