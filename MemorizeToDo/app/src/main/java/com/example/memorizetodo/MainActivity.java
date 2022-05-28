package com.example.memorizetodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizetodo.database.AppDatabase;
import com.example.memorizetodo.database.TaskEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {
    //For logging
    private static final String TAG = MainActivity.class.getSimpleName();
    //Member variables for the adapter and RecyclerView
    private TaskAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //Variable for database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView=findViewById(R.id.recyclerViewTasks);
        //Set the Layout Manager for the RecyclerView to be a linear layout,
        //which measures and positions items within a RecyclerView into a Linear List
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //Initialise the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
        Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
        An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
        and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //View holder- that holds the view
                //swipeDir- direction of swipe

                //First step is to obtain the instance of executor, and then execute a
                //new Runnable using the diskIO executor
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //we need to obtain the position of the view (item) that
                        //was deleted
                        //For this, we obtain the position using getBindingAdapterPosition()
                        int position=viewHolder.getBindingAdapterPosition();
                        /*
                        If you are calling this in the context of an
                        Adapter, you probably want to call
                        getBindingAdapterPosition() or if you want the
                        position as RecyclerView sees it, you should call
                        getAbsoluteAdapterPosition().
                         */
                        //Call the deleteTask method of TaskDao
                        List<TaskEntry> tasks=mAdapter.getTasks();
                        mDb.taskDao().deleteTask(tasks.get(position));

                        /*
                        WE DON'T NEED TO CALL THE retrieveTasks() METHOD AFTER
                        DELETING BECAUSE, AFTER EACH DELETION (or UPDATE) IN THE
                        DATABASE THE onChanged() METHOD WILL BE TRIGGERED AUTOMATICALLY
                         */
                       /* retrieveTasks();//to update the UI*/
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);
        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a new Intent and start the activity AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                Log.i("###","Starting Add task activity");
                startActivity(addTaskIntent);
            }
        });

        //Get the database instance
        mDb=AppDatabase.getInstance(getApplicationContext());

        //Call the retrieveTasks method
        //It was earlier called again and again on each update
        //and in the onResume() method.
        setUpViewModel();
    }
    /**
    * This method is called after this activity has been paused and restarted.
    * Often, this is after new Data has been inserted through an AddTaskActiviy
     * so this re-quires the database data for any changes.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        /*Now we need not call this after each resumption
        Therefore move call to retrieveTasks() to onCreate().
         */
        //retrieveTasks();//to update the UI
    }
    //Rename the retrieveTasks method to setUpViewModel
    private void setUpViewModel() {
        //--------------------------------------------------------
//        //LiveData runs on the separate thread by default.
//        //Therefore we can remove the Executor.
//        /*
//        Note that for other operations such as insert, update and delete, we
//        need not observe the database. Therefore we will not use LiveData with them.
//        We will keep on using the Executors with those operations.
//         */
//        final LiveData<List<TaskEntry>> tasks=  mDb.taskDao().loadAllTasks();
//        //call the observe method on the Tasks variable.
//        tasks.observe(this, new Observer<List<TaskEntry>>() {
//            @Override
//            public void onChanged(List<TaskEntry> taskEntries) {
//                Log.i(TAG+"###","Updating the task list in adapter, onChanged() triggered");
//                    mAdapter.setTasks(taskEntries);
//            }
//        });//Requires two parameters-
//                        //A lifecycle owner and an observer
//-----------------------------------------------------------------------------------
        /*
        We don't need to call the loadAllTasks
        method since we are using ViewModel
         */
        //In order to get the ViewModel, we need to just call
        //the call the ViewModel's providers of this activity,
        //and pass the ViewModel class as the parameter.

        //MainViewModel viewModel= ViewModelProvider.of(this).get(MainViewModel.class);
        MainViewModel viewModel=new ViewModelProvider(this).get(MainViewModel.class);
        //Now we can retrieve our live data object using the getTasks()
        //method from the ViewModel
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                Log.i(TAG+"###","Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
            }
        });

    }
    //Called when an item is clicked in the list of tasks
    @Override
    public void onItemClickListener(int itemId) {
        //Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent taskIntent=new Intent(MainActivity.this,AddTaskActivity.class);
        //Put the data of the clicked item as an extra
        taskIntent.putExtra(AddTaskActivity.EXTRA_TASK_ID,itemId);
        startActivity(taskIntent);
    }
}