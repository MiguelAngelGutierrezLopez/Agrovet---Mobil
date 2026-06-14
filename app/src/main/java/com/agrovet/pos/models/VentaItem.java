package com.agrovet.pos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "venta_items",
        foreignKeys = @ForeignKey(entity = Venta.class,
                parentColumns = "id",
                childColumns = "venta_id",
                onDelete = ForeignKey.CASCADE))
public class VentaItem {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "venta_id")
    private Integer ventaId;

    @ColumnInfo(name = "producto_id")
    private Integer productoId;

    @ColumnInfo(name = "nombre_producto")
    private String nombreProducto;

    @ColumnInfo(name = "cantidad")
    private int cantidad;

    @ColumnInfo(name = "precio_unitario")
    private double precioUnitario;

    @ColumnInfo(name = "total")
    private double total;

    public VentaItem() {}

    public VentaItem(Integer ventaId, CartItem cartItem) {
        this.ventaId = ventaId;
        this.productoId = cartItem.getProducto().getId();
        this.nombreProducto = cartItem.getProducto().getNombre();
        this.cantidad = cartItem.getCantidad();
        this.precioUnitario = cartItem.getProducto().getPrecio();
        this.total = cartItem.getTotal();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
