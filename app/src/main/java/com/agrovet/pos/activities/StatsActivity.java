package com.agrovet.pos.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.agrovet.pos.R;
import com.agrovet.pos.models.Movimiento;
import com.agrovet.pos.models.Producto;
import com.agrovet.pos.viewmodels.MovimientoViewModel;
import com.agrovet.pos.viewmodels.ProductoViewModel;
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

public class StatsActivity extends AppCompatActivity {

    private HorizontalBarChart barChart;
    private PieChart pieChart;
    private ProductoViewModel productoViewModel;
    private MovimientoViewModel movimientoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        movimientoViewModel = new ViewModelProvider(this).get(MovimientoViewModel.class);

        loadBarChartData();
        loadPieChartData();
    }

    private void loadBarChartData() {
        productoViewModel.getProductos().observe(this, productos -> {
            if (productos == null || productos.isEmpty()) return;

            // Ordenar por stock descendente
            List<Producto> sorted = new ArrayList<>(productos);
            Collections.sort(sorted, (p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()));

            // Tomar los top 5
            List<Producto> top5 = sorted.subList(0, Math.min(5, sorted.size()));

            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (int i = 0; i < top5.size(); i++) {
                entries.add(new BarEntry(i, (float)top5.get(i).getStock()));
                labels.add(top5.get(i).getNombre());
            }

            BarDataSet dataSet = new BarDataSet(entries, "Cantidad en Inventario");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(12f);

            BarData data = new BarData(dataSet);
            barChart.setData(data);
            
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(top5.size());
            
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
