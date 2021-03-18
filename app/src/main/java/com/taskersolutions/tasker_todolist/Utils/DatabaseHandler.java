package com.taskersolutions.tasker_todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Paint;

import com.taskersolutions.tasker_todolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ARCHIVE_TABLE = "archive";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, " + STATUS + " INTEGER)";
    private static final String CREATE_ARCHIVE_TABLE = "CREATE TABLE " + ARCHIVE_TABLE + "(" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, " + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
        db.execSQL(CREATE_ARCHIVE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //  Drop the older tables
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ARCHIVE_TABLE);
        // create table again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task, String table) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(table, null, cv);
    }

    public List<ToDoModel> getAllTasks(String table) {
        List<ToDoModel> taskList = new ArrayList<>();

        Cursor cur = null;
        // accesses database safely - incase app is closed part way through function
        db.beginTransaction();
        try {
            // return all rows from database without criteria
            String query = "SELECT * FROM " + table;
            cur = db.rawQuery(query, null);
            if (cur != null) {
                // if cursor is on first row
                if(cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);

                    } while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status, String table) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(table, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task, String table) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(table, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id, String table) {
        db.delete(table, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void moveTask(int id, String task, int status, String newTable, String oldTable) {
        ContentValues cv = new ContentValues();
        // get the task and task status and save in cv
        cv.put(TASK, task);
        cv.put(STATUS, status);
        db.insert(newTable, null, cv);
        deleteTask(id, oldTable);

        //db.execSQL("INSERT INTO " + newTable + " (task, status) SELECT task, status FROM "
        //        + oldTable + " WHERE id=" + id);
    }

    public void clearTable(String table) {
        db.execSQL("DELETE FROM " + table);

    }

}
