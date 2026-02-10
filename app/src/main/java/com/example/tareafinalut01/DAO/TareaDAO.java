package com.example.tareafinalut01.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tareafinalut01.Entidades.Tarea;

import java.util.List;

@Dao
public interface TareaDAO {
    @Query("SELECT * FROM tareas")
    LiveData<List<Tarea>> getAll();

    @Query("SELECT * FROM tareas WHERE _id IN (:tarIds)")
    List<Tarea> loadAllByIds(int[] tarIds);

    @Query("SELECT * FROM tareas WHERE titulo LIKE :titulo LIMIT 1")
    Tarea findByTitulo(String titulo);
    @Query("SELECT AVG(progreso) FROM tareas")
    LiveData<Double> getAverageProgress();

    @Query("SELECT COUNT(*) FROM tareas WHERE progreso = 100")
    LiveData<Integer> getCompletedCount();

    @Query("SELECT COUNT(*) FROM tareas WHERE prioritaria = 1")
    LiveData<Integer> getPriorityCount();

    @Query("SELECT COUNT(*) FROM tareas")
    LiveData<Integer> getTotalCount();
    @Insert
    void insertAll(Tarea... tareas);

    @Delete
    void delete(Tarea tarea);

    @Update
    void update(Tarea tarea);
}
