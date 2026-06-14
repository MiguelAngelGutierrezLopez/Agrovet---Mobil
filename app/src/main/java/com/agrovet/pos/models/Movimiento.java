package com.agrovet.pos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reporte_caja",
        indices = {@Index(name = "idx_reporte_caja_categoria", value = {"categoria"})})
public class Movimiento {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private transient Integer id;

    @ColumnInfo(name = "ingresos", defaultValue = "0.00")
    private Double ingresos;

    @ColumnInfo(name = "razon_ingreso")
    private String razonIngreso;

    @ColumnInfo(name = "fecha_ingreso")
    private String fechaIngreso;

    @ColumnInfo(name = "egresos", defaultValue = "0.00")
    private Double egresos;

    @ColumnInfo(name = "razon_egreso")
    private String razonEgreso;

    @ColumnInfo(name = "fecha_egreso")
    private String fechaEgreso;

    @ColumnInfo(name = "categoria", defaultValue = "otros")
    private String categoria;

    @com.google.gson.annotations.SerializedName("id")
    @ColumnInfo(name = "server_id")
    private Integer serverId;

    @ColumnInfo(name = "is_synced", defaultValue = "0")
    private boolean isSynced = false;

    public Movimiento() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Double getIngresos() { return ingresos; }
    public void setIngresos(Double ingresos) { this.ingresos = ingresos; }

    public String getRazonIngreso() { return razonIngreso; }
    public void setRazonIngreso(String razonIngreso) { this.razonIngreso = razonIngreso; }

    public String getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Double getEgresos() { return egresos; }
    public void setEgresos(Double egresos) { this.egresos = egresos; }

    public String getRazonEgreso() { return razonEgreso; }
    public void setRazonEgreso(String razonEgreso) { this.razonEgreso = razonEgreso; }

    public String getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(String fechaEgreso) { this.fechaEgreso = fechaEgreso; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getServerId() { return serverId; }
    public void setServerId(Integer serverId) { this.serverId = serverId; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }

    public String getRazon() { 
        if (razonIngreso != null && !razonIngreso.isEmpty()) return razonIngreso;
        return razonEgreso != null ? razonEgreso : "";
    }
    
    public String getFecha() { 
        if (razonIngreso != null && !razonIngreso.isEmpty()) return fechaIngreso != null ? fechaIngreso : "";
        return fechaEgreso != null ? fechaEgreso : "";
    }
    
    public double getMonto() { 
        double ing = ingresos != null ? ingresos : 0.0;
        double egr = egresos != null ? egresos : 0.0;
        return Math.max(ing, egr);
    }
    
    public String getTipo() { 
        if (razonIngreso != null && !razonIngreso.isEmpty()) return "Ingreso";
        return "Egreso";
    }
}
