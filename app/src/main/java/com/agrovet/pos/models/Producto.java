package com.agrovet.pos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "productos")
public class Producto {
    @SerializedName("Nombre")
    @ColumnInfo(name = "Nombre")
    private String nombre;

    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "Codigo")
    private long codigo;

    @SerializedName("Categoria")
    @ColumnInfo(name = "Categoria")
    private String categoria;

    @SerializedName("Precio_venta")
    @ColumnInfo(name = "Precio venta")
    private Integer precioVenta;

    @SerializedName("Unidades")
    @ColumnInfo(name = "Unidades")
    private Integer unidades;

    @SerializedName("Presentacion")
    @ColumnInfo(name = "Presentacion")
    private String presentacion;

    public Producto() {
        this.nombre = "";
        this.categoria = "";
        this.presentacion = "";
    }

    public Producto(String nombre, long codigo, String categoria, Integer precioVenta, Integer unidades, String presentacion) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.categoria = categoria;
        this.precioVenta = precioVenta;
        this.unidades = unidades;
        this.presentacion = presentacion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public long getCodigo() { return codigo; }
    public void setCodigo(long codigo) { this.codigo = codigo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Integer precioVenta) { this.precioVenta = precioVenta; }

    public Integer getUnidades() { return unidades; }
    public void setUnidades(Integer unidades) { this.unidades = unidades; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public int getId() { return (int) codigo; }
    public double getPrecio() { return precioVenta != null ? precioVenta.doubleValue() : 0.0; }
    public int getStock() { return unidades != null ? unidades : 0; }
    public String getProveedorTelefono() { return ""; }
}
