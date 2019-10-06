package pl.mobileturtle.easyshoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.mobileturtle.easyshoppinglist.data.ShoppingListData;
import pl.mobileturtle.easyshoppinglist.data.ViewModel;

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_NEED_LIST_LABEL = 1;
    public static final int VIEW_TYPE_ALREADY_HAVE_LIST_LABEL = 2;
    public static final int VIEW_TYPE_ITEMS = 3;
    public static final int ACTION_OPEN_PRODUCT_ACTIVITY = 1;
    public static final int ACTION_DELETE_ITEM = 2;
    private List<ShoppingListData> data;
    private ClickListener clickListener;

    public ShoppingListAdapter(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view;
        switch (viewType) {
            case VIEW_TYPE_NEED_LIST_LABEL:
                view = LayoutInflater.from(context).inflate(R.layout.need_list_label, viewGroup, false);
                return new NeedListLabelViewHolder(view);
            case VIEW_TYPE_ALREADY_HAVE_LIST_LABEL:
                view = LayoutInflater.from(context).inflate(R.layout.already_have_list_label, viewGroup, false);
                return new AlreadyHaveListLabelViewHolder(view);
            default:
                view = LayoutInflater.from(context).inflate(R.layout.shopping_list_item, viewGroup, false);
                return new ItemsViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemsViewHolder) {
            ((ItemsViewHolder) holder).populate(data.get(position).getProductName());
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public class NeedListLabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.button_add) ImageButton addButton;

        private NeedListLabelViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            addButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(-1, ACTION_OPEN_PRODUCT_ACTIVITY);
        }
    }

    public class AlreadyHaveListLabelViewHolder extends RecyclerView.ViewHolder {
        private AlreadyHaveListLabelViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_name) TextView itemName;
        @BindView(R.id.button_delete) ImageButton buttonDelete;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void populate(String name) {
            itemName.setText(name);
            buttonDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = data.get(getAdapterPosition()).getId();
            clickListener.onClick(id, ACTION_DELETE_ITEM);
        }
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(data, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void onItemStopMoving(int starterPosition, int targetPosition) {
        if(starterPosition < targetPosition){
            ViewModel.changeShoppingListItemsPosition(data.get(targetPosition).getId(),
                    data.get(targetPosition-1).getId());
        } else if (starterPosition > targetPosition){
            ViewModel.changeShoppingListItemsPosition(data.get(targetPosition).getId(),
                    data.get(targetPosition+1).getId());
        }
    }

    public void onItemDismiss(int position) {
        ViewModel.deleteShoppingListItemById(data.get(position).getId());
    }

    public interface ClickListener{
        void onClick(int id, int actionType);
    }

    public void updateData(List<ShoppingListData> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
