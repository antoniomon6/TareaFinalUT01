package com.example.tareafinalut01.Actividades;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.tareafinalut01.Entidades.Tarea;
import com.example.tareafinalut01.R;

public class DescripcionActivity extends BaseActivity {

    private TextView tvTitulo, tvDescripcion;
    private TextView tvDoc, tvImg, tvAud, tvVid;
    private Tarea tarea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descripcion);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.detalle_tarea);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTitulo = findViewById(R.id.tv_titulo_detalle);
        tvDescripcion = findViewById(R.id.tv_descripcion_detalle);
        tvDoc = findViewById(R.id.tv_name_doc);
        tvImg = findViewById(R.id.tv_name_img);
        tvAud = findViewById(R.id.tv_name_aud);
        tvVid = findViewById(R.id.tv_name_vid);

        if (getIntent() != null && getIntent().hasExtra("tarea")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                tarea = getIntent().getParcelableExtra("tarea", Tarea.class);
            } else {
                tarea = getIntent().getParcelableExtra("tarea");
            }
            cargarDatos();
        }
    }

    private void cargarDatos() {
        if (tarea != null) {
            tvTitulo.setText(tarea.getTitulo());
            tvDescripcion.setText(tarea.getDescripcion() != null ? tarea.getDescripcion() : "Sin descripción");

            tvDoc.setText(getFileName(tarea.getUriDocumento(), "Ningún documento"));
            tvImg.setText(getFileName(tarea.getUriImagen(), "Ninguna imagen"));
            tvAud.setText(getFileName(tarea.getUriAudio(), "Ningún audio"));
            tvVid.setText(getFileName(tarea.getUriVideo(), "Ningún vídeo"));
        }
    }

    private String getFileName(String uriString, String defaultText) {
        if (uriString == null || uriString.isEmpty()) return defaultText;

        Uri uri = Uri.parse(uriString);
        String result = null;

        if ("file".equals(uri.getScheme())) {
            result = uri.getLastPathSegment();
        } else if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        return result != null ? result : defaultText;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
