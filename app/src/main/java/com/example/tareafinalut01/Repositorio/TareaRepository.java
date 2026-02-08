package com.example.tareafinalut01.Repositorio;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.tareafinalut01.DAO.TareaDAO;
import com.example.tareafinalut01.BD.BaseDatosApp;
import com.example.tareafinalut01.Entidades.Tarea;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * El Repositorio actúa como un intermediario entre las fuentes de datos (la BD de Room)
 * y el resto de la aplicación (los ViewModels). Abstrae la lógica de acceso a datos.
 */
public class TareaRepository {

    private TareaDAO tareaDAO;
    private LiveData<List<Tarea>> listaTareas;


    /**
     * Constructor.
     *
     * @param application El contexto de la aplicación.
     */
    public TareaRepository(Application application) {
        // Obtenemos la instancia de la base de datos y del DAO.
        BaseDatosApp db = BaseDatosApp.getInstance(application);
        tareaDAO = db.tareaDAO();
        // Inicializamos la lista de tareas. Room la mantendrá actualizada.
        listaTareas = tareaDAO.getAll();
    }

    /**
     * Devuelve todas las tareas como LiveData.
     * La UI observará este dato y se actualizará automáticamente ante cambios.
     * Room se encarga de que esta consulta se ejecute en un hilo de fondo.
     */
    public LiveData<List<Tarea>> getAllTareas() {
        return listaTareas;
    }

    /**
     * Inserta una nueva tarea en la base de datos usando un hilo secundario.
     *
     * @param tarea La tarea a insertar.
     */
    public void insert(Tarea tarea) {
        tareaDAO.insertAll(tarea);
    }

    /**
     * Elimina una tarea de la base de datos usando un hilo secundario.
     *
     * @param tarea La tarea a eliminar.
     */
    public void delete(Tarea tarea) {
            tareaDAO.delete(tarea);
    }
}
