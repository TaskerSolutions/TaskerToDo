package com.taskersolutions.tasker_todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.taskersolutions.tasker_todolist.Adapter.ToDoAdapter;
import com.taskersolutions.tasker_todolist.Model.ToDoModel;
import com.taskersolutions.tasker_todolist.Utils.DatabaseHandler;
import com.taskersolutions.tasker_todolist.Utils.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity implements DialogCloseListener {



    private DatabaseHandler db;
  
    private List<ToDoModel> taskList;
    private List<ToDoModel> archiveList;
    private RecyclerView archiveRecyclerView;
    private ToDoAdapter archiveAdapter;

    private Button toDoButton;
    private FloatingActionButton fabDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        //hide action bar
        /*getSupportActionBar().hide();


        db = new DatabaseHandler(this);
        db.openDatabase();

        // create tasks list arrays
        taskList = new ArrayList<>();
        archiveList = new ArrayList<>();
        // fill taskList with tasks from database
        taskList = db.getAllTasks("todo");
        archiveList = db.getAllTasks("archive");

        // define recycler views & set layout manager
        archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
        archiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create adapter
        //archiveAdapter = new ToDoAdapter(db, MainActivity.this, archiveList, taskList);

        /*create touch helper for editing, deleting & moving tasks
        ItemTouchHelper.Callback tasksCallback =
                new RecyclerItemTouchHelper(tasksAdapter, tasksRecyclerView);
        ItemTouchHelper tasksTouchHelper = new ItemTouchHelper(tasksCallback);
        tasksAdapter.setTouchHelper(tasksTouchHelper);

        // attach touch helper, task adapter & recycler view to each other
        tasksTouchHelper.attachToRecyclerView(tasksRecyclerView);
        tasksRecyclerView.setAdapter(tasksAdapter);

        // fill view with task list and update
        tasksAdapter.setTasks(taskList);

         *



        // define to do button and set on click listener
        toDoButton = findViewById(R.id.toDoButton);
        toDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change view to archive
                setContentView(R.layout.activity_main);
            }
        });

        // define floating action button and set on click listener
        fabDelete = findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete all tasks in archive

            }
        });

         */

    }


    // update task list when dialog is closed
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        //archiveList = db.getAllTasks("archive");
        //archiveAdapter.setTasks(archiveList);
    }

}