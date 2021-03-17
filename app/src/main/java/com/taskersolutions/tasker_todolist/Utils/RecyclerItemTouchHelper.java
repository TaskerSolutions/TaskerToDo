package com.taskersolutions.tasker_todolist.Utils;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.taskersolutions.tasker_todolist.Adapter.ToDoAdapter;
import com.taskersolutions.tasker_todolist.R;
import com.taskersolutions.tasker_todolist.Utils.ItemTouchHelperAdapter;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemTouchHelper extends ItemTouchHelper.Callback {

    private final ToDoAdapter adapter;
    private RecyclerView recycler;

    public RecyclerItemTouchHelper(ToDoAdapter adapter, RecyclerView recycler) {
        this.adapter = adapter;
        this.recycler = recycler;
    }

    // return false, because long press is handled in recyclerview view holder
    @Override
    public boolean isLongPressDragEnabled() {  return false;  }

    // return true so swiping the views is enabled
    @Override
    public boolean isItemViewSwipeEnabled() { return true;  }

    // clear view is called when an item that is being dragged is let go of by the user
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        // do things here when drag is let go
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        // do things while drag is in progress
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {  }
    }

    // return what types of movement the touch helper can handle
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // define drag flags as UP and DOWN
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        // define swipe flags for left and right as START and END
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        // return flags
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // pass movement parameters 'old position' , 'new position'
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.START) {
            //delete item
            adapter.deleteItem(recycler, position, "todo");
        } else {
            //edit item
            adapter.editItem(position);
        }
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_edit);
            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(),
                    R.color.color1));
        } else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_delete);
            background = new ColorDrawable(Color.RED);
        }

        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        // swiping to the right
        if (dX > 0) {
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(
                    itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom()
            );
        }
        // swiping to the left
        else if (dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(
                    itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom()
            );
        }
        // not swiped in any direction
        else {
            background.setBounds(0,0,0,0);
        }

        background.draw(c);
        icon.draw(c);
    }

}