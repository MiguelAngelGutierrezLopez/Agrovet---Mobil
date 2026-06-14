package com.agrovet.pos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Proveedores")
public class Proveedor {
    @ColumnInfo(name = "Nombre")
    private String nombre;

    @PrimaryKey
    @ColumnInfo(name = "Credencial")
    private long credencial;

    @ColumnInfo(name = "Empresa")
    private String empresa;

    @ColumnInfo(name = "Productos")
    private String productos;

    public Proveedor() {
        this.nombre = "";
        this.empresa = "";
        this.productos = "";
    }

    public Proveedor(String nombre, long credencial, String empresa, String productos) {
        this.nombre = nombre;
        this.credencial = credencial;
        this.empresa = empresa;
        this.productos = productos;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public long getCredencial() { return credencial; }
    public void setCredencial(long credencial) { this.credencial = credencial; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getProductos() { return productos; }
    public void setProductos(String productos) { this.productos = productos; }

    // Helpers for Activity compatibility
    public String getId() { return String.valueOf(credencial); }
    public String getTelefono() { return String.valueOf(credencial); }
    public String getNombreEmpresa() { return empresa; }
    public String getNombreProveedor() { return nombre; }
}
