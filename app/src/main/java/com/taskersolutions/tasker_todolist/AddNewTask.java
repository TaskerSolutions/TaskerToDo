package com.taskersolutions.tasker_todolist;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.taskersolutions.tasker_todolist.Model.ToDoModel;
import com.taskersolutions.tasker_todolist.Utils.DatabaseHandler;

import java.util.Objects;

import androidx.core.content.ContextCompat;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;

    // returns object of add new class task so it can be used in main activity
    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    //saved instance state checks if the item already exists in memory
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle SavedInstanceState) {
        super.onViewCreated(view, SavedInstanceState);
        newTaskText = getView().findViewById(R.id.newTaskText);
        newTaskText.setFocusableInTouchMode(true);
        newTaskText.requestFocus();
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        // set button to disabled by default
        newTaskSaveButton.setEnabled(false);
        newTaskSaveButton.setTextColor(Color.GRAY);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            assert task != null;
            // enable button if task length is > 0
            if (task.length() > 0) {
                Log.e("TASK LENGTH: ", String.valueOf(task.length()));
                newTaskSaveButton.setEnabled(true);
                newTaskSaveButton.setTextColor(Color.BLACK);
            }
            // place cursor focus at end of existing text
            newTaskText.setSelection(newTaskText.getText().length());
        }

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() <= 0) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(Color.BLACK);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text, "todo");
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    db.insertTask(task, "todo");
                }
                dismiss();
            }
        });

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener) {
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }

}
