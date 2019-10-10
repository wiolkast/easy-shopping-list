package pl.mobileturtle.easyshoppinglist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.mobileturtle.easyshoppinglist.data.ProductEntry;
import pl.mobileturtle.easyshoppinglist.data.ViewModel;
import pl.mobileturtle.easyshoppinglist.widget.WidgetService;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class ProductsActivity extends AppCompatActivity implements ProductsAdapter.ClickListener, ViewModel.ViewModelInsertProductCallback {
    @Nullable @BindView(R.id.rv_products) RecyclerView recyclerView;
    @Nullable @BindView(R.id.fab) FloatingActionButton fab;
    @Nullable @BindView(R.id.tv_title) TextView tvTitle;
    @Nullable @BindView(R.id.edit_text) EditText editText;
    @Nullable @BindView(R.id.tv_delete_confirmation) TextView tvDeleteConfirmation;
    @Nullable @BindView(R.id.tv_add_confirmation) TextView tvAddConfirmation;
    @Nullable @BindView(R.id.button_save) Button buttonSave;
    @Nullable @BindView(R.id.button_cancel) Button buttonCancel;
    @Nullable @BindView(R.id.button_delete) Button buttonDelete;
    @Nullable @BindView(R.id.button_add) Button buttonAdd;
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    @BindString(R.string.add_new_product) String newProductTitle;
    @BindString(R.string.edit_product) String editProductTitle;
    @BindString(R.string.edit_text_product_hint) String editProductHint;
    @BindString(R.string.delete_product_confirmation) String deleteProductConfirmation;
    @BindString(R.string.add_similar_product_confirmation) String addSimilarProductConfirmation;
    @BindString(R.string.empty_product_name_error) String emptyProductNameError;
    @BindString(R.string.product_added_info) String productAddedInfo;
    private ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setHomeActionContentDescription(R.string.up_button_description);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ProductsAdapter(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        viewModel.loadAllProducts().observe(this, new Observer<List<ProductEntry>>() {
            @Override
            public void onChanged(@Nullable List<ProductEntry> productEntries) {
                adapter.updateData(productEntries);
                Intent widgetUpdateIntent = new Intent(getApplicationContext(), WidgetService.class);
                startService(widgetUpdateIntent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductDialog(newProductTitle, null, true);
            }
        });
    }

    @Override
    public void onClick(ProductEntry product, int actionType) {
        switch (actionType) {
            case ProductsAdapter.ACTION_ADD_TO_LIST:
                ViewModel.addProductToList(product);
                Toast toast = Toast.makeText(getApplicationContext(), productAddedInfo, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case ProductsAdapter.ACTION_EDIT:
                openProductDialog(editProductTitle, product, false);
                break;
            case ProductsAdapter.ACTION_DELETE:
                openDeleteConfirmationDialog(product);
                break;
        }
    }

    private void openProductDialog(String title, ProductEntry product, Boolean isNewProduct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_list_or_product, null);
        ButterKnife.bind(this, dialogView);
        tvTitle.setText(title);
        editText.setHint(editProductHint);
        if (product != null) {
            editText.setText(product.getProductName());
        }
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                if (name.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), emptyProductNameError, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (isNewProduct) {
                    ProductEntry product = new ProductEntry(name);
                    ViewModel.insertProduct(product, ProductsActivity.this);
                    alertDialog.dismiss();
                } else {
                    product.setProductName(name);
                    ViewModel.updateProduct(product);
                    alertDialog.dismiss();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void similarProductsDetected(ProductEntry product, List<ProductEntry> similarProducts) {
        String result = " ";
        for(ProductEntry productEntry: similarProducts){
            result = result.concat(productEntry.getProductName());
            result = result.concat(", ");
        }
        result = result.substring(0, result.length()-2);
        openAddSimilarConfirmationDialog(product, result);
    }

    private void openAddSimilarConfirmationDialog(ProductEntry product, String similarProducts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_similar_confirmation, null);
        ButterKnife.bind(this, dialogView);
        tvAddConfirmation.setText(addSimilarProductConfirmation.concat(similarProducts));
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewModel.insertProduct(product, null);
                alertDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void openDeleteConfirmationDialog(ProductEntry product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_confirmation, null);
        ButterKnife.bind(this, dialogView);
        tvDeleteConfirmation.setText(deleteProductConfirmation);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewModel.deleteProduct(product);
                alertDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
