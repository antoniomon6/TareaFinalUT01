package com.example.tareafinalut01.Repositorio;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.tareafinalut01.DAO.TareaDAO;
import com.example.tareafinalut01.BD.BaseDatosApp;
import com.example.tareafinalut01.Entidades.Tarea;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TareaRepository {

    private TareaDAO tareaDAO;
    private LiveData<List<Tarea>> listaTareas;

    public TareaRepository(Application application) {
        // Obtenemos la instancia de la base de datos y del DAO.
        BaseDatosApp db = BaseDatosApp.getInstance(application);
        tareaDAO = db.tareaDAO();
        // Inicializamos la lista de tareas. Room la mantendrá actualizada.
        listaTareas = tareaDAO.getAll();
    }

    public LiveData<List<Tarea>> getAllTareas() {
        return listaTareas;
    }

    public void insert(Tarea tarea) {
        tareaDAO.insertAll(tarea);
    }

    public void delete(Tarea tarea) {
        tareaDAO.delete(tarea);
    }

    public void update(Tarea tarea) {
        tareaDAO.update(tarea);
    }
}
