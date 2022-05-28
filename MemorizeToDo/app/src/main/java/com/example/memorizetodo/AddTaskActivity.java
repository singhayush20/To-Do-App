package com.example.memorizetodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.memorizetodo.database.AppDatabase;
import com.example.memorizetodo.database.TaskEntry;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String Instance_TASK_ID = "instanceTaskId";
    //Constants for Priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;

    private int mTaskId = DEFAULT_TASK_ID;

    //Member variable for the database
    private AppDatabase mDb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initViews();
        /*
        Initialise the database variable by calling the
        getInstance method.
         */
        mDb = AppDatabase.getInstance(getApplicationContext());

        Log.i(TAG + "###", "Add Task Activity started and views are initialised\n mTaskId=" + mTaskId);
        if (savedInstanceState != null && savedInstanceState.containsKey(Instance_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(Instance_TASK_ID, DEFAULT_TASK_ID);
        }
        Log.i(TAG + "###", "mTaskId after checking the condition=" + mTaskId);
        Intent intent = getIntent();
        Log.i(TAG+" ###","intent.hasExtra(EXTRA_TASK_ID): "+intent.hasExtra(EXTRA_TASK_ID));
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            Log.i(TAG+" ###","entered inner if");
            mButton.setText(R.string.update_button);
            if (mTaskId == DEFAULT_TASK_ID) {
                //populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);


//-------------------------------------------------------

//                //LiveData executes on a separate thread
//                //Not required when using ViewModel with LiveData
//                //final LiveData<TaskEntry> task=mDb.taskDao().loadTaskById(mTaskId);
//                task.observe(this, new Observer<TaskEntry>() {
//                    @Override
//                    public void onChanged(TaskEntry taskEntry) {
//                        //Remove the observer from the LiveData object because
//                        //we don't need an observer
//                        task.removeObserver(this);
//                        Log.i(TAG+"###","onChanged() triggered, receiving database update from" +
//                                "LiveData");
//                        populateUI(taskEntry);
//                    }
//                });
                /*-------------------------------------------------------------------------------------*/
                //Create an Instance of the ViewModelFactory class
                //Pass the database and the task id to the constructor
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
                //We can create a ViewModel similarly as we did for MainViewModel
                final AddTaskViewModel viewModel = new ViewModelProvider(this, factory).get(AddTaskViewModel.class);
                //Now aur LiveData object is cast in the ViewModel
                //So we can retrieve it again after rotation without needing to
                //re-query our database
                viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(TaskEntry taskEntry) {
                        //Remove the observer since it will be observed in MainActivity
                        viewModel.getTask().removeObserver(this);
                        Log.i(TAG + "###", "populating UI after removing observer");
                        populateUI(taskEntry);

                    }
                });

            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(Instance_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        if (task == null) {
            Log.i(TAG+" ###","task is null, return from populateUI");
            return;
        }
        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String description = mEditText.getText().toString();
        //Checking if the task description is empty. This is should not be allowed!
        if(description.equals("")||description.equals("\n"))
        {
            Toast.makeText(this, "Empty task not allowed!", Toast.LENGTH_SHORT).show();
            //Do not call finish(), as it will add the empty task to the database.
            //Instead return after displaying the toast.
            return;
        }
        int priority = getPriorityFromViews();
        Date date = new Date();
        //Make the TaskEntry Variable final so that it is visible from inside the
        //run() method.
        final TaskEntry taskEntry = new TaskEntry(description, priority, date);

        //We will add the task to our database on a separate thread using the
        //Executor
        //Get an instance of the AppExecutor to get the diskIO executor and
        //execute a new Runnable. That will contain our database logic.
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID)//to add new task else update
                    mDb.taskDao().insertTask(taskEntry);
                else {
                    taskEntry.setId(mTaskId);
                    mDb.taskDao().updateTask(taskEntry);
                }
                //Assuming we want to return to our list, call the finish method.
               finish();
            }
        });

    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    @SuppressLint("NonConstantResourceId")
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }
}
