package com.example.memorizetodo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.memorizetodo.database.AppDatabase;
import com.example.memorizetodo.database.TaskEntry;

/**
 * This class will extend the ViewModel class
 * unlike the MainViewModel class which extends
 * AndroidViewModel class
 */
public class AddTaskViewModel extends ViewModel {
    //private member variable for the LiveData
    private LiveData<TaskEntry> task;

    /*
    Initialise the task in the constructor using a
    relevant call to the database.
     */
    public AddTaskViewModel(AppDatabase database,int taskId)
    {
        task=database.taskDao().loadTaskById(taskId);
    }
    public LiveData<TaskEntry> getTask()
    {
        return task;
    }
}
