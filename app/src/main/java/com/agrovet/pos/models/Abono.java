package com.agrovet.pos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "abonos",
        indices = {
            @Index(name = "idx_abonos_credito", value = {"credito_id"}),
            @Index(name = "idx_abonos_fecha", value = {"fecha"}),
            @Index(name = "idx_abonos_cliente_cedula", value = {"cliente_cedula"}),
            @Index(name = "idx_abonos_venta_id", value = {"venta_id"})
        })
public class Abono {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "credito_id")
    private Integer creditoId;

    @NonNull
    @ColumnInfo(name = "venta_id")
    private Integer ventaId;

    @NonNull
    @ColumnInfo(name = "cliente_cedula")
    private String clienteCedula;

    @ColumnInfo(name = "monto")
    private double monto;

    @NonNull
    @ColumnInfo(name = "fecha")
    private String fecha;

    @NonNull
    @ColumnInfo(name = "metodo_pago", defaultValue = "efectivo")
    private String metodoPago;

    @ColumnInfo(name = "referencia")
    private String referencia;

    @ColumnInfo(name = "usuario_registra")
    private String usuarioRegistra;

    @ColumnInfo(name = "observacion")
    private String observacion;

    @NonNull
    @ColumnInfo(name = "fecha_registro", defaultValue = "CURRENT_TIMESTAMP")
    private String fechaRegistro;

    public Abono() {
        this.creditoId = 0;
        this.ventaId = 0;
        this.clienteCedula = "";
        this.fecha = "";
        this.metodoPago = "efectivo";
        this.fechaRegistro = "";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @NonNull
    public Integer getCreditoId() { return creditoId; }
    public void setCreditoId(@NonNull Integer creditoId) { this.creditoId = creditoId; }

    @NonNull
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(@NonNull Integer ventaId) { this.ventaId = ventaId; }

    @NonNull
    public String getClienteCedula() { return clienteCedula; }
    public void setClienteCedula(@NonNull String clienteCedula) { this.clienteCedula = clienteCedula; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    @NonNull
    public String getFecha() { return fecha; }
    public void setFecha(@NonNull String fecha) { this.fecha = fecha; }

    @NonNull
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(@NonNull String metodoPago) { this.metodoPago = metodoPago; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getUsuarioRegistra() { return usuarioRegistra; }
    public void setUsuarioRegistra(String usuarioRegistra) { this.usuarioRegistra = usuarioRegistra; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    @NonNull
    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(@NonNull String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
