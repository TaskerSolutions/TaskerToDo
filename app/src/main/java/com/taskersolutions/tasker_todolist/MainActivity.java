package com.taskersolutions.tasker_todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.taskersolutions.tasker_todolist.Adapter.ToDoAdapter;
import com.taskersolutions.tasker_todolist.ArchiveActivity;
import com.taskersolutions.tasker_todolist.Model.ToDoModel;
import com.taskersolutions.tasker_todolist.Utils.DatabaseHandler;
import com.taskersolutions.tasker_todolist.Utils.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;
    private List<ToDoModel> taskList;
    private List<ToDoModel> archiveList;

    private RecyclerView tasksRecyclerView;
    private RecyclerView archiveRecyclerView;

    private ToDoAdapter tasksAdapter;
    private ToDoAdapter archiveAdapter;

    private Button archiveButton;
    private Button toDoButton;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabDelete;

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
        taskList = db.getAllTasks("todo");
        archiveList = db.getAllTasks("archive");

        openToDo();
    }

    public void openToDo() {
        // define recycler view
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        // set layout manager
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // create adapter
        tasksAdapter = new ToDoAdapter(db, this, taskList, archiveList);
        // create touch helper for editing, deleting & moving tasks
        ItemTouchHelper.Callback tasksCallback =
                new RecyclerItemTouchHelper(tasksAdapter, tasksRecyclerView, "todo");
        ItemTouchHelper tasksTouchHelper = new ItemTouchHelper(tasksCallback);
        tasksAdapter.setTouchHelper(tasksTouchHelper);
       // attach touch helper, task adapter & recycler view to each other
        tasksTouchHelper.attachToRecyclerView(tasksRecyclerView);
        tasksRecyclerView.setAdapter(tasksAdapter);
        // fill view with task list and update
        tasksAdapter.setTasks(taskList);

        // define floating action button and set on click listener
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open new task interface
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        // define archive button and set on click listener
        archiveButton = findViewById(R.id.archiveButton);
        archiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change view to archive
                setContentView(R.layout.activity_archive);
                openArchive();
                archiveList = db.getAllTasks("archive");
                archiveAdapter.setTasks(archiveList);
            }
        });
    }


    public void openArchive() {
        // if i want to open archive as a new activity
        //Intent intent = new Intent(this, ArchiveActivity.class);
        //startActivity(intent);

        // define recycler view
        archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
        // set layout manager
        archiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // create adapter
        archiveAdapter = new ToDoAdapter(db, this, archiveList, taskList);
        // create touch helper for editing, deleting & moving tasks
        ItemTouchHelper.Callback archiveCallback =
                new RecyclerItemTouchHelper(archiveAdapter, archiveRecyclerView, "archive");
        ItemTouchHelper archiveTouchHelper = new ItemTouchHelper(archiveCallback);
        archiveAdapter.setTouchHelper(archiveTouchHelper);
        // attach touch helper, task adapter & recycler view to each other
        archiveTouchHelper.attachToRecyclerView(archiveRecyclerView);
        archiveRecyclerView.setAdapter(archiveAdapter);
        // fill view with task list and update
        archiveAdapter.setTasks(archiveList);


        // define to do button and set on click listener
        toDoButton = findViewById(R.id.toDoButton);
        toDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change view to To Do
                setContentView(R.layout.activity_main);
                openToDo();
                taskList = db.getAllTasks("todo");
                tasksAdapter.setTasks(taskList);
            }
        });

        // define delete all tasks floating action button and set on click listener
        fabDelete = findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete task confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(archiveAdapter.getContext());
                builder.setTitle("Empty Archive?");
                builder.setMessage("Are you sure you want to delete all tasks from archive?" +
                        "\n\nTHIS CANNOT BE UNDONE.");
                builder.setPositiveButton("Confirm",
                        (dialogInterface, i) -> {
                            //empty database and empty array list
                            db.clearTable("archive");
                            archiveList = db.getAllTasks("archive");
                            archiveAdapter.setTasks(archiveList);
                        });
                builder.setNegativeButton(android.R.string.cancel,
                        (dialogInterface, i) -> {
                            // close dialog box
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
    }


    // update task list when dialog is closed
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks("todo");
        archiveList = db.getAllTasks("archive");
        tasksAdapter.setTasks(taskList);

        //archiveAdapter.setTasks(archiveList);
    }

}