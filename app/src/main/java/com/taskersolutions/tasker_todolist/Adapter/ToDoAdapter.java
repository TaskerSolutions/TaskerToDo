package com.taskersolutions.tasker_todolist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.taskersolutions.tasker_todolist.AddNewTask;
import com.taskersolutions.tasker_todolist.DialogCloseListener;
import com.taskersolutions.tasker_todolist.MainActivity;
import com.taskersolutions.tasker_todolist.Model.ToDoModel;
import com.taskersolutions.tasker_todolist.R;
import com.taskersolutions.tasker_todolist.Utils.DatabaseHandler;
import com.taskersolutions.tasker_todolist.Utils.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static ItemTouchHelper touchHelper;
    private List<ToDoModel> oldList;
    private List<ToDoModel> newList;

    private MainActivity activity;
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity,
                       List<ToDoModel> oldList,
                       List<ToDoModel> newList) {
        this.db = db;
        this.activity = activity;
        this.oldList = oldList;
        this.newList = newList;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        ToDoModel item = oldList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // move checked item to bottom of the list
                    // causing app to crash when new task is added
                    /*
                    if (holder.getAdapterPosition() < oldList.size() - 1) {
                        Collections.swap(oldList, holder.getAdapterPosition(), oldList.size() - 1);
                        notifyItemMoved(holder.getAdapterPosition(), oldList.size() - 1);
                    }

                     */
                    db.updateStatus(item.getId(), 1, "todo");
                } else {
                    // move unchecked item to top of the list
                    /*
                    if (holder.getAdapterPosition() > 0) {
                        Collections.swap(oldList, holder.getAdapterPosition(), 0);
                        notifyItemMoved(holder.getAdapterPosition(), 0);
                    }

                     */
                    db.updateStatus(item.getId(), 0, "todo");
                }
                //Log.e("log:", "item status changed");
            }
        });
    }

    @Override
    public int getItemCount() {
        return oldList.size();
    }

    // helper to convert int to boolean
    private boolean toBoolean(int number) {
        // returns true if number is not 0
        return number != 0;
    }

    public void setTasks(List<ToDoModel> list) {
        this.oldList = list;
        notifyDataSetChanged();
    }

    public void restoreItem(RecyclerView recyclerView, int position) {
        //Log.e("Position:", String.valueOf(position));
        ToDoModel archivedTask = oldList.get(position);
        newList.add(archivedTask);
        db.moveTask(archivedTask.getId(), archivedTask.getTask(), archivedTask.getStatus(), "todo", "archive");

        oldList.remove(position);
        notifyItemRemoved(position);

        //Log.e("Archive List: ", String.valueOf(archiveList.size()));

        Toast.makeText(getContext(), "Restored task: " + archivedTask.getTask(), Toast.LENGTH_SHORT).show();
    }

    public void archiveItem(RecyclerView recyclerView, int position) {
       // Log.e("Position:", String.valueOf(position));
        ToDoModel archivedTask = oldList.get(position);
        newList.add(archivedTask);
        db.moveTask(archivedTask.getId(), archivedTask.getTask(), archivedTask.getStatus(), "archive", "todo");
        oldList.remove(position);
        notifyItemRemoved(position);
        Snackbar.make(recyclerView, "Archived task: " + archivedTask.getTask(), Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        db.moveTask(archivedTask.getId(), archivedTask.getTask(), archivedTask.getStatus(), "todo", "archive");
                        newList.remove(newList.lastIndexOf(archivedTask));
                        oldList.add(position, archivedTask);
                        notifyItemInserted(position);
                    }
                }).show();
    }

    public void editItem(int position) {
        ToDoModel item = oldList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public Context getContext() {
        return activity;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        ToDoModel fromItem = oldList.get(fromPosition);
        oldList.remove(fromItem);
        oldList.add(toPosition, fromItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position) {

    }

    public void setTouchHelper (ItemTouchHelper touchHelper) {
        ToDoAdapter.touchHelper = touchHelper;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener,
            GestureDetector.OnGestureListener {

        CheckBox task;
        GestureDetector gestureDetector;

        ViewHolder(View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.todoCheckBox);

            gestureDetector = new GestureDetector(itemView.getContext(), this);
            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            // where on click event would go
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            touchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }

}
