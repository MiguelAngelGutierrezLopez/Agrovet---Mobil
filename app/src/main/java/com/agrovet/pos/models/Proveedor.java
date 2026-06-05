package com.agrovet.pos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "proveedor")
public class Proveedor {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "telefono")
    private String telefono;

    @NonNull
    @ColumnInfo(name = "nombre_empresa")
    private String nombreEmpresa;

    @NonNull
    @ColumnInfo(name = "nombre_proveedor")
    private String nombreProveedor;

    @ColumnInfo(name = "correo")
    private String correo;

    @ColumnInfo(name = "estado", defaultValue = "activo")
    private String estado;

    @ColumnInfo(name = "fecha_registro", defaultValue = "CURRENT_TIMESTAMP")
    private String fechaRegistro;

    @ColumnInfo(name = "producto")
    private String producto;

    public Proveedor() {
        this.telefono = "";
        this.nombreEmpresa = "";
        this.nombreProveedor = "";
    }

    public Proveedor(@NonNull String telefono, @NonNull String nombreEmpresa, @NonNull String nombreProveedor, String correo, String estado, String fechaRegistro, String producto) {
        this.telefono = telefono;
        this.nombreEmpresa = nombreEmpresa;
        this.nombreProveedor = nombreProveedor;
        this.correo = correo;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.producto = producto;
    }

    @NonNull
    public String getTelefono() { return telefono; }
    public void setTelefono(@NonNull String telefono) { this.telefono = telefono; }

    @NonNull
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(@NonNull String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    @NonNull
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(@NonNull String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    // Helpers for Activity compatibility
    public String getId() { return telefono; }
    public String getProductos() { return producto; }
}
