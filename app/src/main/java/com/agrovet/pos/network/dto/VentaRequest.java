package com.agrovet.pos.network.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VentaRequest {
    @SerializedName("productos")
    private List<ProductoItem> productos;
    
    @SerializedName("subtotal")
    private double subtotal;
    
    @SerializedName("descuento")
    private double descuento;
    
    @SerializedName("total")
    private double total;
    
    @SerializedName("metodo_pago")
    private String metodoPago;
    
    @SerializedName("cliente_cedula")
    private String clienteCedula;
    
    @SerializedName("dinero_entregado")
    private double dineroEntregado;
    
    @SerializedName("es_mixta")
    private boolean esMixta;

    @SerializedName("dias_credito")
    private Integer diasCredito;

    @SerializedName("anticipo")
    private Double anticipo;

    public static class ProductoItem {
        @SerializedName("id")
        private Integer id;
        
        @SerializedName("nombre")
        private String nombre;
        
        @SerializedName("cantidad")
        private int cantidad;
        
        @SerializedName("precio")
        private double precio;
        
        @SerializedName("total")
        private double total;

        public ProductoItem(Integer id, String nombre, int cantidad, double precio, double total) {
            this.id = id;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
            this.total = total;
        }
    }

    // Getters and Setters
    public List<ProductoItem> getProductos() { return productos; }
    public void setProductos(List<ProductoItem> productos) { this.productos = productos; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getClienteCedula() { return clienteCedula; }
    public void setClienteCedula(String clienteCedula) { this.clienteCedula = clienteCedula; }
    public double getDineroEntregado() { return dineroEntregado; }
    public void setDineroEntregado(double dineroEntregado) { this.dineroEntregado = dineroEntregado; }
    public boolean isEsMixta() { return esMixta; }
    public void setEsMixta(boolean esMixta) { this.esMixta = esMixta; }
    public Integer getDiasCredito() { return diasCredito; }
    public void setDiasCredito(Integer diasCredito) { this.diasCredito = diasCredito; }
    public Double getAnticipo() { return anticipo; }
    public void setAnticipo(Double anticipo) { this.anticipo = anticipo; }
}
