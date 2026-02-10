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
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TareaRepository(Application application) {
        BaseDatosApp db = BaseDatosApp.getInstance(application);
        tareaDAO = db.tareaDAO();
        listaTareas = tareaDAO.getAll();
    }

    public void insert(Tarea tarea) {
        executorService.execute(() -> tareaDAO.insertAll(tarea));
    }

    public void update(Tarea tarea) {
        executorService.execute(() -> tareaDAO.update(tarea));
    }

    public void delete(Tarea tarea) {
        executorService.execute(() -> tareaDAO.delete(tarea));
    }

    public LiveData<List<Tarea>> getAllTareas() {
        return listaTareas;
    }
    public LiveData<Double> getAverageProgress() { return tareaDAO.getAverageProgress(); }
    public LiveData<Integer> getCompletedCount() { return tareaDAO.getCompletedCount(); }
    public LiveData<Integer> getPriorityCount() { return tareaDAO.getPriorityCount(); }
    public LiveData<Integer> getTotalCount() { return tareaDAO.getTotalCount(); }



}
