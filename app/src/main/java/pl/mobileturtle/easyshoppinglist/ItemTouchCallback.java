package pl.mobileturtle.easyshoppinglist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchCallback extends ItemTouchHelper.Callback {
    private ShoppingListAdapter adapter;
    private int starterPosition;
    private int targetPosition;

    public ItemTouchCallback(ShoppingListAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        int swipeFlags;

        if (viewHolder instanceof ShoppingListAdapter.NeedListLabelViewHolder ||
                viewHolder instanceof ShoppingListAdapter.AlreadyHaveListLabelViewHolder) {
            dragFlags = 0;
            swipeFlags = 0;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if(target.getAdapterPosition()>0) {
            adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            starterPosition = viewHolder.getAdapterPosition();
            viewHolder.itemView.setTranslationZ(8);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        targetPosition = viewHolder.getAdapterPosition();
        viewHolder.itemView.setTranslationZ(0);
        if(targetPosition!=-1){
            adapter.onItemStopMoving(starterPosition, targetPosition);
        }
        super.clearView(recyclerView, viewHolder);
    }
}
