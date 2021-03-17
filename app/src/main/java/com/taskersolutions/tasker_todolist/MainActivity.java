package com.taskersolutions.tasker_todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.taskersolutions.tasker_todolist.Adapter.ToDoAdapter;
import com.taskersolutions.tasker_todolist.Model.ToDoModel;
import com.taskersolutions.tasker_todolist.Utils.DatabaseHandler;
import com.taskersolutions.tasker_todolist.Utils.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private FloatingActionButton fab;
    private DatabaseHandler db;
    private List<ToDoModel> taskList;
    private List<ToDoModel> archiveList;
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hide action bar
        getSupportActionBar().hide();

        // create database handler & open database
        db = new DatabaseHandler(this);
        db.openDatabase();

        // create tasks list arrays
        taskList = new ArrayList<>();
        archiveList = new ArrayList<>();
        // fill taskList with tasks from database
        taskList = db.getAllTasks();
        // reverse task list // Collections.reverse(taskList);

        // define recycler views & set layout manager
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create adapter
        tasksAdapter = new ToDoAdapter(db, this, taskList, archiveList);

        // create touch helper for editing, deleting & moving tasks
        ItemTouchHelper.Callback tasksCallback =
                new RecyclerItemTouchHelper(tasksAdapter, tasksRecyclerView);
        ItemTouchHelper tasksTouchHelper = new ItemTouchHelper(tasksCallback);
        tasksAdapter.setTouchHelper(tasksTouchHelper);

        // attach touch helper, task adapter & recycler view to each other
        tasksTouchHelper.attachToRecyclerView(tasksRecyclerView);
        tasksRecyclerView.setAdapter(tasksAdapter);

        // fill view with task list and update
        tasksAdapter.setTasks(taskList);

        // define floating action button and set on click listener
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open new task interface
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }


    // update task list when dialog is closed
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        tasksAdapter.setTasks(taskList);
    }

}