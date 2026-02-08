package com.example.tareafinalut01.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tareafinalut01.Entidades.Tarea;

import java.util.List;
@Dao
public interface TareaDAO {
    /**
     * Obtiene todas las tareas de la base de datos.
     * Se utiliza LiveData para que la interfaz de usuario se actualice automáticamente
     * cada vez que los datos en la tabla 'tareas' cambien.
     */
    @Query("SELECT * FROM tareas")
    LiveData<List<Tarea>> getAll();
    /**
     * Obtiene una lista de tareas cuyos identificadores coincidan con los proporcionados.
     * @param tarIds Array de identificadores de las tareas a buscar.
     */
    @Query("SELECT * FROM tareas WHERE _id IN (:tarIds)")
    List<Tarea> loadAllByIds(int[] tarIds);

    /**
     * Busca una tarea específica por su título.
     * @param titulo Título de la tarea (soporta búsqueda parcial con LIKE).
     * @return La primera tarea que coincida con el criterio.
     */
    @Query("SELECT * FROM tareas WHERE titulo LIKE :titulo LIMIT 1")
    Tarea findByTitulo(String titulo);

    /**
     * Inserta una o varias tareas en la base de datos.
     * @param tareas Listado de tareas a insertar.
     */
    @Insert
    void insertAll(Tarea... tareas);

    /**
     * Elimina una tarea específica de la base de datos.
     * @param tarea Objeto tarea a eliminar.
     */
    @Delete
    void delete(Tarea tarea);
}
