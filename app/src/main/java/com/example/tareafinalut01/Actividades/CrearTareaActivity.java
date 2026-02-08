package com.example.tareafinalut01.Actividades;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.tareafinalut01.Fragmentos.FirstFragment;
import com.example.tareafinalut01.R;
import com.example.tareafinalut01.Fragmentos.SecondFragment;
import com.example.tareafinalut01.Entidades.Tarea;

public class CrearTareaActivity extends BaseActivity implements FirstFragment.ComuncacionFragmento1, SecondFragment.ComuncacionFragmento2 {

    FragmentManager manager;
    Tarea tarea;
    int posTarea = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_tarea);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        manager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            // La actividad se crea por primera vez
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("tarea")) {
                // MODO EDICIÓN
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    tarea = intent.getParcelableExtra("tarea", Tarea.class);
                } else {
                    tarea = intent.getParcelableExtra("tarea");
                }
                posTarea = intent.getIntExtra("pos", -1);
            } else {
                // MODO CREACIÓN
                tarea = new Tarea();
            }
            // Mostramos el primer fragmento
            FirstFragment primero = FirstFragment.newInstance(tarea);
            manager.beginTransaction().add(R.id.lineal_frag, primero).commit();
        } else {
            // La actividad se está recreando, restauramos el estado
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                tarea = savedInstanceState.getParcelable("tarea", Tarea.class);
            } else {
                tarea = savedInstanceState.getParcelable("tarea");
            }
            posTarea = savedInstanceState.getInt("posTarea", -1);
        }

        // Ponemos el título después de saber si creamos o editamos
        if (posTarea != -1) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.titulo_editar_tarea);
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.titulo_crear_tarea);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardamos el estado actual antes de que la actividad se destruya
        outState.putParcelable("tarea", tarea);
        outState.putInt("posTarea", posTarea);
    }

    @Override
    public void ir1() {
        FirstFragment primero = FirstFragment.newInstance(tarea);
        manager.beginTransaction().replace(R.id.lineal_frag, primero).commit();
    }

    @Override
    public void guardarDescripcion(String descripcion) {
        if (tarea != null) {
            tarea.setDescripcion(descripcion);
        }
        Intent resultIntent = new Intent();
        if (posTarea != -1) {
            resultIntent.putExtra("EDITAR_TAREA", tarea);
            resultIntent.putExtra("pos", posTarea);
        } else {
            resultIntent.putExtra("NUEVA_TAREA", tarea);
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void guardarTareaSinDescripcion(Tarea tareaSinDescripcion) {
        if (tarea != null) {
            tarea.setTitulo(tareaSinDescripcion.getTitulo());
            tarea.setProgreso(tareaSinDescripcion.getProgreso());
            tarea.setFechaCreacion(tareaSinDescripcion.getFechaCreacion());
            tarea.setFechaLimite(tareaSinDescripcion.getFechaLimite());
            tarea.setPrioritaria(tareaSinDescripcion.isPrioritaria());
        } else {
            this.tarea = tareaSinDescripcion;
        }
        SecondFragment segundo = SecondFragment.newInstance(this.tarea);
        manager.beginTransaction().replace(R.id.lineal_frag, segundo).commit();
    }
}
