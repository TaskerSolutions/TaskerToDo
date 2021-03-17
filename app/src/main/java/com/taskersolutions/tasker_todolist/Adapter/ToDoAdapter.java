package com.taskersolutions.tasker_todolist.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.material.snackbar.Snackbar;
import com.taskersolutions.tasker_todolist.AddNewTask;
import com.taskersolutions.tasker_todolist.DialogCloseListener;
import com.taskersolutions.tasker_todolist.MainActivity;
import com.taskersolutions.tasker_todolist.Model.ToDoModel;
import com.taskersolutions.tasker_todolist.R;
import com.taskersolutions.tasker_todolist.Utils.DatabaseHandler;
import com.taskersolutions.tasker_todolist.Utils.ItemTouchHelperAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static ItemTouchHelper touchHelper;
    private List<ToDoModel> list;
    private List<ToDoModel> archiveList;

    private MainActivity activity;
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity,
                       List<ToDoModel> list,
                       List<ToDoModel> archiveList) {
        this.db = db;
        this.activity = activity;
        this.list = list;
        this.archiveList = archiveList;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        ToDoModel item = list.get(position);
        int id = item.getId();
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    db.updateStatus(id, 1, "todo");

                } else {
                    db.updateStatus(id, 0, "todo");
                }
                //Log.e("log:", "item status changed");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // helper to convert int to boolean
    private boolean toBoolean(int number) {
        // returns true if number is not 0
        return number != 0;
    }

    public void setTasks(List<ToDoModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void deleteItem(RecyclerView recyclerView, int position, String table) {
       // Log.e("Position:", String.valueOf(position));
        ToDoModel archivedTask = list.get(position);
        archiveList.add(archivedTask);
        list.remove(position);
        notifyItemRemoved(position);
        Snackbar.make(recyclerView, "Archived task: " + archivedTask.getTask(), Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        archiveList.remove(archiveList.lastIndexOf(archivedTask));
                        list.add(position, archivedTask);
                        notifyItemInserted(position);
                    }
                }).show();

        /*
        ToDoModel item = list.get(position);
        db.deleteTask(item.getId(), table);
        list.remove(position);
        notifyItemRemoved(position);
        Snackbar.make(recyclerView, "Deleted task: " + item.getTask(), Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                list.add(position, item);
                notifyItemInserted(position);
            }
        }).show();
         */
    }

    public void editItem(int position) {
        ToDoModel item = list.get(position);
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
        ToDoModel fromItem = list.get(fromPosition);
        list.remove(fromItem);
        list.add(toPosition, fromItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        /* delete task confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setTitle("Delete task");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.deleteItem(position, "todo");
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        */
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
