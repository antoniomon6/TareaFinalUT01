package com.example.tareafinalut01;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class BaseActivity extends AppCompatActivity {


    protected boolean tema;

    protected String fuente ;

    protected String criterio ;

    protected boolean orden;

    protected  boolean sd ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tema = prefs.getBoolean("switch_tema", true);
        fuente = prefs.getString("list_fuente", "2");
        criterio = prefs.getString("list_criterio", "2");
        orden = prefs.getBoolean("switch_orden", true);
        sd = prefs.getBoolean("checkbox_sd", false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (tema) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if (!prefs.getString("list_fuente", "2").equals(fuente)) {
            recreate();
        }
        tema = prefs.getBoolean("switch_tema", true);
        fuente = prefs.getString("list_fuente", "2");
        criterio = prefs.getString("list_criterio", "2");
        orden = prefs.getBoolean("switch_orden", true);
        sd = prefs.getBoolean("checkbox_sd", false);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences newPrefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String fontSize = newPrefs.getString("list_fuente", "2");
        float scale;
        switch (fontSize) {
            case "1": // Pequeña
                scale = 0.85f;
                break;
            case "3": // Grande
                scale = 1.25f;
                break;
            default: // Mediana
                scale = 1.0f;
                break;
        }

        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.fontScale = scale;
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}
