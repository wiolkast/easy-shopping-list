package pl.mobileturtle.easyshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.mobileturtle.easyshoppinglist.data.ListEntry;
import pl.mobileturtle.easyshoppinglist.data.ViewModel;
import pl.mobileturtle.easyshoppinglist.widget.WidgetService;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class ListsActivity extends AppCompatActivity implements ListsAdapter.ClickListener {
    @Nullable @BindView(R.id.rv_lists) RecyclerView recyclerView;
    @Nullable @BindView(R.id.fab) FloatingActionButton fab;
    @Nullable @BindView(R.id.tv_title) TextView tvTitle;
    @Nullable @BindView(R.id.edit_text) EditText editText;
    @Nullable @BindView(R.id.tv_delete_confirmation) TextView tvDeleteConfirmation;
    @Nullable @BindView(R.id.button_save) Button buttonSave;
    @Nullable @BindView(R.id.button_cancel) Button buttonCancel;
    @Nullable @BindView(R.id.button_delete) Button buttonDelete;
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    @BindString(R.string.add_new_list) String newListTitle;
    @BindString(R.string.edit_list) String editListTitle;
    @BindString(R.string.edit_text_list_hint) String editListHint;
    @BindString(R.string.delete_list_confirmation) String deleteListConfirmation;
    @BindString(R.string.delete_active_list_error) String deleteActiveListError;
    @BindString(R.string.empty_list_name_error) String emptyListNameError;
    private ListsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        viewModel.loadAllLists().observe(this, new Observer<List<ListEntry>>() {
            @Override
            public void onChanged(@Nullable List<ListEntry> listEntries) {
                adapter.updateData(listEntries);
                Intent widgetUpdateIntent = new Intent(getApplicationContext(), WidgetService.class);
                startService(widgetUpdateIntent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ListsAdapter(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListDialog(newListTitle, null, true);
            }
        });
    }

    @Override
    public void onClick(ListEntry list, int actionType) {
        switch (actionType) {
            case ListsAdapter.ACTION_CHECKED:
                ViewModel.changeActiveList(list);
                break;
            case ListsAdapter.ACTION_EDIT:
                openListDialog(editListTitle, list, false);
                break;
            case ListsAdapter.ACTION_DELETE:
                if (list.getIsActive()) {
                    Toast toast = Toast.makeText(this, deleteActiveListError, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    openDeleteConfirmationDialog(list);
                }
                break;
        }
    }

    private void openListDialog(String title, ListEntry list, Boolean isNewList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_list_or_product, null);
        ButterKnife.bind(this, dialogView);
        tvTitle.setText(title);
        editText.setHint(editListHint);
        if (list != null) {
            editText.setText(list.getListName());
        }
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                if (name.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), emptyListNameError, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (isNewList) {
                    ListEntry list = new ListEntry(name, true);
                    ViewModel.insertList(list);
                    alertDialog.dismiss();
                } else {
                    list.setListName(name);
                    ViewModel.updateList(list);
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

    private void openDeleteConfirmationDialog(ListEntry list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_confirmation, null);
        ButterKnife.bind(this, dialogView);
        tvDeleteConfirmation.setText(deleteListConfirmation);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewModel.deleteList(list);
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
