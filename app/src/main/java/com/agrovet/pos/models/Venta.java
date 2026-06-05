package com.agrovet.pos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ventas")
public class Venta {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "numero_venta")
    private Integer numeroVenta;

    @NonNull
    @ColumnInfo(name = "fecha_dia")
    private String fechaDia;

    @NonNull
    @ColumnInfo(name = "fecha_hora")
    private String fechaHora;

    @NonNull
    @ColumnInfo(name = "nombre_cliente")
    private String nombreCliente;

    @ColumnInfo(name = "direccion_cliente")
    private String direccionCliente;

    @ColumnInfo(name = "telefono_cliente")
    private String telefonoCliente;

    @NonNull
    @ColumnInfo(name = "tipo_pago")
    private String tipoPago;

    @ColumnInfo(name = "cliente_cedula")
    private String clienteCedula;

    @ColumnInfo(name = "subtotal", defaultValue = "0.00")
    private double subtotal;

    @ColumnInfo(name = "descuento", defaultValue = "0.00")
    private double descuento;

    @ColumnInfo(name = "total", defaultValue = "0.00")
    private double total;

    @ColumnInfo(name = "dias_credito")
    private Integer diasCredito;

    @ColumnInfo(name = "submetodo_banco")
    private String submetodoBanco;

    @ColumnInfo(name = "usuario_id", defaultValue = "1")
    private Integer usuarioId;

    @ColumnInfo(name = "estado", defaultValue = "completada")
    private String estado;

    public Venta() {
        this.fechaDia = "";
        this.fechaHora = "";
        this.nombreCliente = "";
        this.tipoPago = "";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getNumeroVenta() { return numeroVenta; }
    public void setNumeroVenta(Integer numeroVenta) { this.numeroVenta = numeroVenta; }

    @NonNull
    public String getFechaDia() { return fechaDia; }
    public void setFechaDia(@NonNull String fechaDia) { this.fechaDia = fechaDia; }

    @NonNull
    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(@NonNull String fechaHora) { this.fechaHora = fechaHora; }

    @NonNull
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(@NonNull String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDireccionCliente() { return direccionCliente; }
    public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    @NonNull
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(@NonNull String tipoPago) { this.tipoPago = tipoPago; }

    public String getClienteCedula() { return clienteCedula; }
    public void setClienteCedula(String clienteCedula) { this.clienteCedula = clienteCedula; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Integer getDiasCredito() { return diasCredito; }
    public void setDiasCredito(Integer diasCredito) { this.diasCredito = diasCredito; }

    public String getSubmetodoBanco() { return submetodoBanco; }
    public void setSubmetodoBanco(String submetodoBanco) { this.submetodoBanco = submetodoBanco; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Helpers for Activity compatibility
    public String getTicket() { return String.valueOf(numeroVenta != null ? numeroVenta : id); }
    public String getFecha() { return fechaDia + " " + fechaHora; }
    public String getCliente() { return nombreCliente; }
    public String getMetodoPago() { return tipoPago; }
}
