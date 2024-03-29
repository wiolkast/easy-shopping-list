package pl.mobileturtle.easyshoppinglist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.mobileturtle.easyshoppinglist.data.ShoppingListData;
import pl.mobileturtle.easyshoppinglist.data.ViewModel;
import pl.mobileturtle.easyshoppinglist.widget.WidgetService;

public class MainActivity extends AppCompatActivity implements ShoppingListAdapter.ClickListener {
    @BindView(R.id.rv_shopping_list) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.adView) AdView adView;
    private ShoppingListAdapter adapter;
    private List<ShoppingListData> shoppinglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ShoppingListAdapter(this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new ItemTouchCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        viewModel.loadShoppingListData().observe(this, new Observer<List<ShoppingListData>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingListData> data) {
                adapter.updateData(data);
                shoppinglist = data;
                Intent widgetUpdateIntent = new Intent(getApplicationContext(), WidgetService.class);
                startService(widgetUpdateIntent);
            }
        });
        viewModel.getShoppingListName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String listName) {
                    toolbarTitle.setText(listName);
            }
        });

        // Populate sample database on the first run
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = sharedPreferences.getBoolean("is_first_run", true);
        if (isFirstRun) {
            ViewModel.populateSampleDatabase(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_first_run", false);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_show_lists:
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                Intent intent = new Intent(this, ListsActivity.class);
                startActivity(intent, bundle);
                break;
            case R.id.action_send_email:
                composeEmail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void composeEmail() {
        String message = "";
        for(ShoppingListData item: shoppinglist){
            switch (item.getType()) {
                case ShoppingListAdapter.VIEW_TYPE_NEED_LIST_LABEL:
                    message = message.concat(getString(R.string.need_label) + "\n");
                    break;
                case ShoppingListAdapter.VIEW_TYPE_ALREADY_HAVE_LIST_LABEL:
                    message = message.concat("\n\n" + getString(R.string.already_have_label) + "\n");
                    break;
                case ShoppingListAdapter.VIEW_TYPE_ITEMS:
                    message = message.concat("\n" + item.getProductName());
                    break;
            }
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(int id, int actionType) {
        switch (actionType) {
            case ShoppingListAdapter.ACTION_OPEN_PRODUCT_ACTIVITY:
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                Intent intent = new Intent(this, ProductsActivity.class);
                startActivity(intent, bundle);
                break;
            case ShoppingListAdapter.ACTION_DELETE_ITEM:
                ViewModel.deleteShoppingListItemById(id);
                break;
        }
    }
}
