package com.example.memorizetodo;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.memorizetodo.database.AppDatabase;
import com.example.memorizetodo.database.TaskEntry;

import java.util.List;

/* We are going to use this ViewModel to cache our
list of TaskEntry objects which are wrapped in a LiveData object.
 */
public class MainViewModel extends AndroidViewModel {
    //for Logging
    private final String TAG=AndroidViewModel.class.getSimpleName();
    private LiveData<List<TaskEntry>> tasks;
    public MainViewModel(@NonNull Application application) {
        super(application);
        //We need to initialise the tasks list
        //Obtain a database object and call the loadAllTasks method
        //in our TaskDao
        AppDatabase database=AppDatabase.getInstance(this.getApplication());
        tasks=database.taskDao().loadAllTasks();
        Log.i(TAG+"###","Actively retrieving the tasks from the database");
    }
    //A public getter method
    public LiveData<List<TaskEntry>> getTasks()
    {
        return tasks;
    }
}
