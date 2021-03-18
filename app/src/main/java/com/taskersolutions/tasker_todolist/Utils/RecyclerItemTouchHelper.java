package com.taskersolutions.tasker_todolist.Utils;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.taskersolutions.tasker_todolist.Adapter.ToDoAdapter;
import com.taskersolutions.tasker_todolist.R;
import com.taskersolutions.tasker_todolist.Utils.ItemTouchHelperAdapter;

import org.w3c.dom.Text;

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
    private String currentView;

    public RecyclerItemTouchHelper(ToDoAdapter adapter, RecyclerView recycler, String currentView) {
        this.adapter = adapter;
        this.recycler = recycler;
        this.currentView = currentView;
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
            // archive item
            if (currentView == "archive") {
                //Log.e("Message:", "restoring item");
                adapter.restoreItem(recycler, position);
            } else {
                //Log.e("Message:", "archiving item");
                adapter.archiveItem(recycler, position);
            }
        } else {
            //edit item
            adapter.editItem(position);
        }
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;



        int textX = 0;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        String text = "";
        float textWidth = paint.measureText(text);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_edit);
            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(),
                    R.color.color1));

            text = "Edit";
            textX = itemView.getLeft() + icon.getIntrinsicWidth() + 90;

        } else {
            if (currentView == "archive") {
                background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(),
                        R.color.color8));
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_restore);
            } else {
                background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(),
                        R.color.color7));
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_archive);
            }
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

            if (currentView == "archive") {
                text = "Un-Archive";
                textWidth = paint.measureText(text);
                textX = itemView.getRight() - icon.getIntrinsicWidth() - 90 - (int)textWidth;
            } else {
                text = "Archive";
                textWidth = paint.measureText(text);
                textX = itemView.getRight() - icon.getIntrinsicWidth() - 90 - (int)textWidth;
            }

        }
        // not swiped in any direction
        else {
            text = "" ;
            background.setBounds(0,0,0,0);
        }

        background.draw(c);
        icon.draw(c);
        c.drawText(text, textX, itemView.getTop() + itemView.getHeight() / 2 + 20 , paint);

    }

}