package com.agrovet.pos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Clientes")
public class Cliente {
    @SerializedName("Nombre")
    @NonNull
    @ColumnInfo(name = "Nombre")
    private String nombre;

    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "Cedula")
    private long cedula;

    @SerializedName("Correo")
    @NonNull
    @ColumnInfo(name = "Correo")
    private String correo;

    @SerializedName("Telefono")
    @ColumnInfo(name = "telefono")
    private long telefono;

    @SerializedName("Deuda")
    @ColumnInfo(name = "Deuda")
    private int deuda;

    public Cliente() {
        this.nombre = "";
        this.correo = "";
    }

    public Cliente(@NonNull String nombre, long cedula, @NonNull String correo, long telefono, int deuda) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.correo = correo;
        this.telefono = telefono;
        this.deuda = deuda;
    }

    @NonNull
    public String getNombre() { return nombre; }
    public void setNombre(@NonNull String nombre) { this.nombre = nombre; }

    public long getCedula() { return cedula; }
    public void setCedula(long cedula) { this.cedula = cedula; }

    @NonNull
    public String getCorreo() { return correo; }
    public void setCorreo(@NonNull String correo) { this.correo = correo; }

    public long getTelefono() { return telefono; }
    public void setTelefono(long telefono) { this.telefono = telefono; }

    public int getDeuda() { return deuda; }
    public void setDeuda(int deuda) { this.deuda = deuda; }

    public String getId() { return String.valueOf(cedula); }
}
