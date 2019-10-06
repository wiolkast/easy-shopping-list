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
import pl.mobileturtle.easyshoppinglist.data.ProductEntry;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {
    private List<ProductEntry> productEntries;
    private ClickListener clickListener;
    public static final int ACTION_ADD_TO_LIST = 1;
    public static final int ACTION_EDIT = 2;
    public static final int ACTION_DELETE = 3;

    public ProductsAdapter(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        holder.populate(productEntries.get(position));
    }

    @Override
    public int getItemCount() {
        if (productEntries == null) {
            return 0;
        }
        return productEntries.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.product_name) TextView productName;
        @BindView(R.id.button_add) ImageButton buttonAdd;
        @BindView(R.id.button_edit) ImageButton buttonEdit;
        @BindView(R.id.button_delete) ImageButton buttonDelete;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void populate(ProductEntry product) {
            productName.setText(product.getProductName());
            buttonAdd.setOnClickListener(this);
            buttonEdit.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ProductEntry product = productEntries.get(getAdapterPosition());
            switch(v.getId()){
                case R.id.button_add:
                    clickListener.onClick(product, ACTION_ADD_TO_LIST);
                    break;
                case R.id.button_edit:
                    clickListener.onClick(product, ACTION_EDIT);
                    break;
                case R.id.button_delete:
                    clickListener.onClick(product, ACTION_DELETE);
                    break;
            }
        }
    }

    public interface ClickListener{
        void onClick(ProductEntry product, int actionType);
    }

    public void updateData(List<ProductEntry> productEntries) {
        this.productEntries = productEntries;
        notifyDataSetChanged();
    }
}
