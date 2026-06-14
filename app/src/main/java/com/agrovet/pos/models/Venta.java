package com.agrovet.pos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.List;

@Entity(tableName = "ventas",
        indices = {
            @Index(name = "idx_ventas_cliente", value = {"cliente_cedula"}),
            @Index(name = "idx_ventas_fecha", value = {"fecha_dia"}),
            @Index(name = "idx_ventas_numero", value = {"numero_venta"})
        })
public class Venta {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private transient Integer id; // Usamos 'id' para que coincida con getId/setId y Room lo encuentre

    @ColumnInfo(name = "numero_venta")
    @com.google.gson.annotations.SerializedName(value = "ticket_numero", alternate = {"numero", "nro_ticket"})
    private Integer numeroVenta;

    @NonNull
    @ColumnInfo(name = "fecha_dia")
    @com.google.gson.annotations.SerializedName(value = "fecha", alternate = {"fecha_dia", "created_at"})
    private String fechaDia;

    @NonNull
    @ColumnInfo(name = "fecha_hora")
    @com.google.gson.annotations.SerializedName(value = "hora", alternate = {"fecha_hora"})
    private String fechaHora;

    @NonNull
    @ColumnInfo(name = "nombre_cliente")
    @com.google.gson.annotations.SerializedName(value = "cliente_nombre", alternate = {"cliente", "nombre"})
    private String nombreCliente;

    @ColumnInfo(name = "direccion_cliente")
    private String direccionCliente;

    @ColumnInfo(name = "telefono_cliente")
    private String telefonoCliente;

    @NonNull
    @ColumnInfo(name = "tipo_pago")
    @com.google.gson.annotations.SerializedName(value = "metodo_pago", alternate = {"tipo_pago", "pago"})
    private String tipoPago;

    @ColumnInfo(name = "cliente_cedula")
    @com.google.gson.annotations.SerializedName(value = "cliente_cedula", alternate = {"cedula", "documento"})
    private String clienteCedula;

    @ColumnInfo(name = "subtotal", defaultValue = "0.00")
    @com.google.gson.annotations.SerializedName(value = "subtotal", alternate = {"sub_total", "monto_subtotal"})
    private double subtotal;

    @ColumnInfo(name = "descuento", defaultValue = "0.00")
    @com.google.gson.annotations.SerializedName(value = "descuento", alternate = {"valor_descuento", "monto_descuento"})
    private double descuento;

    @ColumnInfo(name = "total", defaultValue = "0.00")
    @com.google.gson.annotations.SerializedName(value = "total", alternate = {"total_venta", "monto_total", "valor_total"})
    private double total;

    @ColumnInfo(name = "anticipo", defaultValue = "0.00")
    @com.google.gson.annotations.SerializedName(value = "anticipo", alternate = {"pago_inicial", "monto_anticipo"})
    private double anticipo;

    @ColumnInfo(name = "dias_credito")
    @com.google.gson.annotations.SerializedName("dias_credito")
    private Integer diasCredito;

    @ColumnInfo(name = "submetodo_banco")
    private String submetodoBanco;

    @ColumnInfo(name = "usuario_id", defaultValue = "1")
    private Integer usuarioId;

    @ColumnInfo(name = "estado", defaultValue = "completada")
    private String estado;

    @com.google.gson.annotations.SerializedName("id")
    @ColumnInfo(name = "server_id")
    private Integer serverId;

    @ColumnInfo(name = "is_synced", defaultValue = "0")
    private boolean isSynced = false;

    @androidx.room.Ignore
    @com.google.gson.annotations.SerializedName("productos")
    private List<VentaItem> items;

    public Venta() {
        this.fechaDia = "";
        this.fechaHora = "";
        this.nombreCliente = "";
        this.tipoPago = "";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public double getAnticipo() { return anticipo; }
    public void setAnticipo(double anticipo) { this.anticipo = anticipo; }

    public Integer getDiasCredito() { return diasCredito; }
    public void setDiasCredito(Integer diasCredito) { this.diasCredito = diasCredito; }

    public String getSubmetodoBanco() { return submetodoBanco; }
    public void setSubmetodoBanco(String submetodoBanco) { this.submetodoBanco = submetodoBanco; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getServerId() { return serverId; }
    public void setServerId(Integer serverId) { this.serverId = serverId; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }

    public List<VentaItem> getItems() { return items; }
    public void setItems(List<VentaItem> items) { this.items = items; }

    public String getTicket() { return String.valueOf(numeroVenta != null ? numeroVenta : (id != null ? id : "")); }
    public String getFecha() { return fechaDia + " " + fechaHora; }
    public String getCliente() { return nombreCliente; }
    public String getMetodoPago() { return tipoPago; }
}
