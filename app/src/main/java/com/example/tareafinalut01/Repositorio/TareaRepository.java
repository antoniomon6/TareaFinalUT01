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
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public TareaRepository(Application application) {
        BaseDatosApp db = BaseDatosApp.getInstance(application);
        tareaDAO = db.tareaDAO();
        listaTareas = tareaDAO.getAll();
    }
    public LiveData<List<Tarea>> getAllTareas() {
        return listaTareas;
    }
    public void insert(Tarea tarea) {
        executorService.execute(() -> tareaDAO.insertAll(tarea));
    }
    public void delete(Tarea tarea) {
        executorService.execute(() -> tareaDAO.delete(tarea));
    }
    public void update(Tarea tarea) {
        executorService.execute(() -> tareaDAO.update(tarea));
    }

    // --- Métodos para Estadísticas ---
    public LiveData<Integer> getTotalTaskCount() {
        return tareaDAO.getTotalTaskCount();
    }

    public LiveData<Integer> getPriorityTaskCount() {
        return tareaDAO.getPriorityTaskCount();
    }

    public LiveData<Integer> getCompletedTaskCount() {
        return tareaDAO.getCompletedTaskCount();
    }

    public LiveData<Double> getAverageProgress() {
        return tareaDAO.getAverageProgress();
    }
}
