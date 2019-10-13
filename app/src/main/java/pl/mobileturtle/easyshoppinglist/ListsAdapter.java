package pl.mobileturtle.easyshoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.mobileturtle.easyshoppinglist.data.ListEntry;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsViewHolder> {
    private List<ListEntry> listEntries;
    private final ClickListener clickListener;
    public static final int ACTION_CHECKED = 1;
    public static final int ACTION_EDIT = 2;
    public static final int ACTION_DELETE = 3;

    public ListsAdapter(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ListsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListsViewHolder holder, int position) {
        holder.populate(listEntries.get(position));
    }

    @Override
    public int getItemCount() {
        if (listEntries == null) {
            return 0;
        }
        return listEntries.size();
    }

    public class ListsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.button_checked) ImageButton buttonChecked;
        @BindView(R.id.list_name) TextView listName;
        @BindView(R.id.button_edit) ImageButton buttonEdit;
        @BindView(R.id.button_delete) ImageButton buttonDelete;

        private ListsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void populate(ListEntry list) {
            if (list.getIsActive()) {
                buttonChecked.setVisibility(View.VISIBLE);
            } else {
                buttonChecked.setVisibility(View.INVISIBLE);
            }
            listName.setText(list.getListName());
            listName.setOnClickListener(this);
            buttonEdit.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ListEntry list = listEntries.get(getAdapterPosition());
            switch(v.getId()){
                case R.id.list_name:
                    clickListener.onClick(list, ACTION_CHECKED);
                    break;
                case R.id.button_edit:
                    clickListener.onClick(list, ACTION_EDIT);
                    break;
                case R.id.button_delete:
                    clickListener.onClick(list, ACTION_DELETE);
                    break;
            }
        }
    }

    public interface ClickListener{
        void onClick(ListEntry list, int actionType);
    }

    public void updateData(List<ListEntry> listEntries) {
        this.listEntries = listEntries;
        notifyDataSetChanged();
    }
}
