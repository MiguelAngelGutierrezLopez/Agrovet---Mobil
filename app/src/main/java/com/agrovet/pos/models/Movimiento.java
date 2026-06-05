package com.agrovet.pos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reporte_caja")
public class Movimiento {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "ingresos", defaultValue = "0.00")
    private Double ingresos;

    @ColumnInfo(name = "razon_ingreso")
    private String razonIngreso;

    @ColumnInfo(name = "fecha_ingreso")
    private String fechaIngreso;

    @ColumnInfo(name = "categoria", defaultValue = "otros")
    private String categoria;

    @ColumnInfo(name = "egresos", defaultValue = "0.00")
    private Double egresos;

    @ColumnInfo(name = "razon_egreso")
    private String razonEgreso;

    @ColumnInfo(name = "fecha_egreso")
    private String fechaEgreso;

    public Movimiento() {}

    public Movimiento(String razon, String fecha, double monto, String tipo) {
        this.categoria = "otros";
        if ("Ingreso".equals(tipo)) {
            this.ingresos = monto;
            this.razonIngreso = razon;
            this.fechaIngreso = fecha;
            this.egresos = 0.0;
        } else {
            this.egresos = monto;
            this.razonEgreso = razon;
            this.fechaEgreso = fecha;
            this.ingresos = 0.0;
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Double getIngresos() { return ingresos; }
    public void setIngresos(Double ingresos) { this.ingresos = ingresos; }

    public String getRazonIngreso() { return razonIngreso; }
    public void setRazonIngreso(String razonIngreso) { this.razonIngreso = razonIngreso; }

    public String getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getEgresos() { return egresos; }
    public void setEgresos(Double egresos) { this.egresos = egresos; }

    public String getRazonEgreso() { return razonEgreso; }
    public void setRazonEgreso(String razonEgreso) { this.razonEgreso = razonEgreso; }

    public String getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(String fechaEgreso) { this.fechaEgreso = fechaEgreso; }

    // Helpers for Activity compatibility
    public String getRazon() { 
        return (razonIngreso != null && !razonIngreso.isEmpty()) ? razonIngreso : razonEgreso; 
    }
    public String getFecha() { 
        return (fechaIngreso != null && !fechaIngreso.isEmpty()) ? fechaIngreso : (fechaEgreso != null ? fechaEgreso : "");
    }
    public double getMonto() { 
        return (ingresos != null && ingresos > 0) ? ingresos : (egresos != null ? egresos : 0.0); 
    }
    public String getTipo() { 
        return (ingresos != null && ingresos > 0) ? "Ingreso" : "Egreso"; 
    }
}
