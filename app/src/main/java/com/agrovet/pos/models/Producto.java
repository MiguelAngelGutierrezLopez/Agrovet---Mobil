package com.agrovet.pos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos")
public class Producto {
    @ColumnInfo(name = "Nombre")
    private String nombre;

    @PrimaryKey
    @ColumnInfo(name = "Codigo")
    private long codigo;

    @ColumnInfo(name = "Categoria")
    private String categoria;

    @ColumnInfo(name = "Precio venta")
    private Integer precioVenta; // Nullable in DB (INTEGER, notNull=false)

    @ColumnInfo(name = "Unidades")
    private Integer unidades; // Nullable in DB (INTEGER, notNull=false)

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

    // Helpers for Activity compatibility
    public int getId() { return (int) codigo; }
    public double getPrecio() { return precioVenta != null ? precioVenta.doubleValue() : 0.0; }
    public int getStock() { return unidades != null ? unidades : 0; }
    public String getProveedorTelefono() { return ""; }
}
