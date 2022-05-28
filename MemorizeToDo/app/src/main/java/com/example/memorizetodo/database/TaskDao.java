package com.example.memorizetodo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Dao instead. Marks the class as a Data Access
 * Object. ...
 * The class marked with @Dao should either be an
 * interface or an abstract class. At compile time,
 * Room will generate an implementation of this class
 * when it is referenced by a Database .
 */
@Dao
public interface TaskDao {
    /*Using LiveData, we can get notified when their is a
    change in the database. This way we need not query the database
    everytime.
     */
    @Query("SELECT * FROM task ORDER BY priority")
    LiveData<List<TaskEntry>> loadAllTasks();
    @Insert
    void insertTask(TaskEntry taskEntry);
    @Update (onConflict= OnConflictStrategy.REPLACE)
    void updateTask(TaskEntry taskEntry);
    @Delete
    void deleteTask(TaskEntry taskEntry);

    //To get a TaskEntry object using its id.
    //Also add live data for this query and
    //Make changes in the AddTaskActivity
    @Query("SELECT * FROM task WHERE id= :id")
    LiveData<TaskEntry> loadTaskById(int id);
}
