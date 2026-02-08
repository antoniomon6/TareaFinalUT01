package com.example.tareafinalut01.BD;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tareafinalut01.DAO.TareaDAO;
import com.example.tareafinalut01.Entidades.Tarea;

@Database(entities = {Tarea.class}, version = 1, exportSchema = false)
public abstract class BaseDatosApp extends RoomDatabase {
    private static BaseDatosApp INSTANCIA;

    public static BaseDatosApp getInstance(Context contexto) {
        if (INSTANCIA == null) {
            INSTANCIA = Room.databaseBuilder(
                    contexto.getApplicationContext(),
                    BaseDatosApp.class,"db_tareas")
                    .build();
        }
        return INSTANCIA;
    }
    public  static void destroyInstance() {
        INSTANCIA = null;
    }
    public abstract TareaDAO tareaDAO();
}
