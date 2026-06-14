package com.agrovet.pos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "productos",
        indices = {@Index(name = "idx_productos_proveedor", value = {"proveedor"})})
public class Producto {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private transient Integer id;

    @NonNull
    @SerializedName("nombre")
    @ColumnInfo(name = "nombre")
    private String nombre;

    @SerializedName("descripcion")
    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @NonNull
    @SerializedName("categoria")
    @ColumnInfo(name = "categoria")
    private String categoria;

    @SerializedName("cantidad")
    @ColumnInfo(name = "cantidad", defaultValue = "0")
    private Integer cantidad;

    @SerializedName("presentacion")
    @ColumnInfo(name = "presentacion")
    private String presentacion;

    @SerializedName("proveedor")
    @ColumnInfo(name = "proveedor")
    private String proveedor;

    @SerializedName("precio_costo")
    @ColumnInfo(name = "precio_costo")
    private Integer precioCosto;

    @SerializedName("precio_venta")
    @ColumnInfo(name = "precio_venta")
    private Integer precioVenta;

    @SerializedName("id")
    @ColumnInfo(name = "server_id")
    private Integer serverId;

    @ColumnInfo(name = "is_synced", defaultValue = "0")
    private boolean isSynced = false;

    public Producto() {
    }

    public Producto(Integer id, @NonNull String nombre, String descripcion, @NonNull String categoria, Integer cantidad, String presentacion, String proveedor, Integer precioCosto, Integer precioVenta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.presentacion = presentacion;
        this.proveedor = proveedor;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @NonNull
    public String getNombre() { return nombre; }
    public void setNombre(@NonNull String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @NonNull
    public String getCategoria() { return categoria; }
    public void setCategoria(@NonNull String categoria) { this.categoria = categoria; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public Integer getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(Integer precioCosto) { this.precioCosto = precioCosto; }

    public Integer getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Integer precioVenta) { this.precioVenta = precioVenta; }

    public Integer getServerId() { return serverId; }
    public void setServerId(Integer serverId) { this.serverId = serverId; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }

    public double getPrecio() { return precioVenta != null ? precioVenta.doubleValue() : 0.0; }
    public int getStock() { return cantidad != null ? cantidad : 0; }
    public String getCodigo() { return String.valueOf(id != null ? id : ""); }
    public String getProveedorTelefono() { return proveedor != null ? proveedor : ""; }
}
