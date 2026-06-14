package com.agrovet.pos.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.agrovet.pos.R;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.models.Venta;
import com.agrovet.pos.viewmodels.MovimientoViewModel;
import com.agrovet.pos.viewmodels.VentaViewModel;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsActivity extends BaseActivity {

    private HorizontalBarChart barChart;
    private PieChart pieChart;
    private MovimientoViewModel movimientoViewModel;
    private VentaViewModel ventaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        ventaViewModel = new ViewModelProvider(this).get(VentaViewModel.class);
        movimientoViewModel = new ViewModelProvider(this).get(MovimientoViewModel.class);

        setupDrawer();

        loadIncomeChartData();
        loadPieChartData();
    }

    private void loadIncomeChartData() {
        ventaViewModel.getAllVentas().observe(this, ventas -> {
            if (ventas == null || ventas.isEmpty()) return;

            double bancoTotal = 0, contadoTotal = 0, creditoTotal = 0;
            for (Venta v : ventas) {
                String tipo = v.getTipoPago();
                if ("Banco".equalsIgnoreCase(tipo)) bancoTotal += v.getTotal();
                else if ("Contado".equalsIgnoreCase(tipo) || "Efectivo".equalsIgnoreCase(tipo)) contadoTotal += v.getTotal();
                else if ("Crédito".equalsIgnoreCase(tipo)) creditoTotal += v.getTotal();
            }

            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0, (float)contadoTotal));
            entries.add(new BarEntry(1, (float)creditoTotal));
            entries.add(new BarEntry(2, (float)bancoTotal));

            List<String> labels = new ArrayList<>();
            labels.add("Contado");
            labels.add("Crédito");
            labels.add("Banco");

            BarDataSet dataSet = new BarDataSet(entries, "Ingresos Totales por Método");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(12f);

            BarData data = new BarData(dataSet);
            barChart.setData(data);
            
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(3);
            
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getDescription().setEnabled(false);
            barChart.setFitBars(true);
            barChart.animateY(1000);
            barChart.invalidate();
        });
    }

    private void loadPieChartData() {
        movimientoViewModel.getAllMovimientos().observe(this, movimientos -> {
            if (movimientos == null || movimientos.isEmpty()) return;

            // Agrupar por razón/concepto sumando montos solo de egresos
            Map<String, Double> montosPorConcepto = new HashMap<>();
            for (Movimiento m : movimientos) {
                if (m.getEgresos() != null && m.getEgresos() > 0) {
                    String razon = m.getRazon();
                    if (razon == null || razon.trim().isEmpty()) razon = "Varios";
                    double monto = m.getEgresos();
                    montosPorConcepto.put(razon, montosPorConcepto.getOrDefault(razon, 0.0) + monto);
                }
            }

            if (montosPorConcepto.isEmpty()) {
                pieChart.setData(null);
                pieChart.invalidate();
                return;
            }

            List<PieEntry> entries = new ArrayList<>();
            for (Map.Entry<String, Double> entry : montosPorConcepto.entrySet()) {
                entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Motivos de Egreso");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSet.setValueTextSize(14f);

            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Egresos por Concepto");
            pieChart.animateXY(1000, 1000);
            pieChart.invalidate();
        });
    }
}
