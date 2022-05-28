package com.example.memorizetodo;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.memorizetodo.database.AppDatabase;

public class AddTaskViewModelFactory implements ViewModelProvider.Factory {
    /*
    The factory class will require two Member Variables
    1. A database object
    2. The id of the task that needs to be updated.
     */
    private final AppDatabase mDb;
    private final int mTaskId;
    public AddTaskViewModelFactory(AppDatabase database,int taskId)
    {
        mDb=database;
        mTaskId=taskId;
    }

    /**
     * Override the create() method
     * @param modelClass
     * @param <T>:Return type class should be a subclass of ViewModel
     * @return: a new AddTaskViewModel that uses the parameters in its constructor
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTaskViewModel(mDb,mTaskId);
    }
}
