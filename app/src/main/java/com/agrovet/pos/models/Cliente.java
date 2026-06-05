package com.agrovet.pos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cliente")
public class Cliente {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "cedula")
    private String cedula;

    @NonNull
    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "telefono")
    private String telefono;

    @ColumnInfo(name = "correo")
    private String correo;

    @ColumnInfo(name = "direccion")
    private String direccion;

    @NonNull
    @ColumnInfo(name = "fecha_creacion", defaultValue = "CURRENT_TIMESTAMP")
    private String fechaCreacion;

    public Cliente() {
        this.cedula = "";
        this.nombre = "";
        this.fechaCreacion = "";
    }

    public Cliente(@NonNull String cedula, @NonNull String nombre, String telefono, String correo, String direccion, @NonNull String fechaCreacion) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaCreacion = fechaCreacion;
    }

    @NonNull
    public String getCedula() { return cedula; }
    public void setCedula(@NonNull String cedula) { this.cedula = cedula; }

    @NonNull
    public String getNombre() { return nombre; }
    public void setNombre(@NonNull String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @NonNull
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(@NonNull String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Helper for Activity compatibility
    public String getId() { return cedula; }
}
