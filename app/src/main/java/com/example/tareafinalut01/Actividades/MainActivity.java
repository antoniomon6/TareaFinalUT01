package com.example.tareafinalut01.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tareafinalut01.R;

public class MainActivity extends BaseActivity {


    Button btnEmpezar;
    ImageView ivIcono;
    TextView tvEslogan;

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
        ivIcono = findViewById(R.id.imageView);
        tvEslogan = findViewById(R.id.textView);

        btnEmpezar.setOnClickListener(this::empezarApp);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ivIcono != null)
            ivIcono.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animacion_icono));
        if (tvEslogan != null)
            tvEslogan.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animacion_texto));
    }


    public void empezarApp(View view) {
        Intent intent = new Intent(MainActivity.this, ListadoTareasActivity.class);
        startActivity(intent);
    }

}