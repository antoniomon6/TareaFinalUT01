package com.example.tareafinalut01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {


    Button btnEmpezar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_principal);
        }
        btnEmpezar = findViewById(R.id.btnEmpezar);
        btnEmpezar.setOnClickListener(this::empezarApp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actPreferences();
    }

    public void actPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tema = sharedPreferences.getBoolean("switch_tema", true);

        Integer fuente = Integer.parseInt(sharedPreferences.getString("list_fuente", "2"));

        Integer criterio = Integer.parseInt(sharedPreferences.getString("list_criterio", "2"));

        boolean orden = sharedPreferences.getBoolean("switch_orden", true);

        boolean sd = sharedPreferences.getBoolean("checkbox_sd", false);

    }
    public void empezarApp(View view){
        Intent intent = new Intent(MainActivity.this, ListadoTareasActivity.class);
        startActivity(intent);
    }

}