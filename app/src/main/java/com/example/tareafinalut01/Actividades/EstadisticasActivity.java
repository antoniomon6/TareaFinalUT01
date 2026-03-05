package com.example.tareafinalut01.Actividades;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.tareafinalut01.R;
import com.example.tareafinalut01.Repositorio.TareaRepository;

import java.util.Locale;

public class EstadisticasActivity extends BaseActivity {

    private TextView tvTotal, tvProgresoMedio, tvPrioritarias, tvCompletadas;
    private ProgressBar pbProgresoMedio;
    private TareaRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Estadísticas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new TareaRepository(getApplication());

        tvTotal = findViewById(R.id.tv_total_tareas);
        tvProgresoMedio = findViewById(R.id.tv_progreso_medio);
        tvPrioritarias = findViewById(R.id.tv_tareas_prioritarias);
        tvCompletadas = findViewById(R.id.tv_tareas_completadas);
        pbProgresoMedio = findViewById(R.id.pb_progreso_medio);
        observarEstadisticas();
    }

    private void observarEstadisticas() {
        repository.getTotalTaskCount().observe(this, count -> {
            tvTotal.setText(String.valueOf(count != null ? count : 0));
        });

        repository.getPriorityTaskCount().observe(this, count -> {
            tvPrioritarias.setText(String.valueOf(count != null ? count : 0));
        });

        repository.getCompletedTaskCount().observe(this, count -> {
            tvCompletadas.setText(String.valueOf(count != null ? count : 0));
        });

        repository.getAverageProgress().observe(this, avg -> {
            double average = (avg != null) ? avg : 0.0;
            tvProgresoMedio.setText(String.format(Locale.getDefault(), "%.1f%%", average));
            pbProgresoMedio.setProgress((int) average);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
