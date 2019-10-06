package pl.mobileturtle.easyshoppinglist.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import pl.mobileturtle.easyshoppinglist.R;
import pl.mobileturtle.easyshoppinglist.ShoppingListAdapter;
import pl.mobileturtle.easyshoppinglist.utilities.AppExecutors;

public class ViewModel extends AndroidViewModel {

    private static ShoppingListDatabase db;
    private static LiveData<List<ListEntry>> lists;
    private static LiveData<List<ProductEntry>> products;
    private static LiveData<List<ShoppingListData>> data;
    private static LiveData<String> listName;

    public ViewModel(@NonNull Application application) {
        super(application);
        db = ShoppingListDatabase.getInstance(application);
        lists = db.listDao().loadAllLists();
        products = db.productDao().loadAllIProducts();
        data = db.shoppingListDao().loadShoppingListData();
        listName = db.listDao().getShoppingListName();
    }

    public LiveData<List<ListEntry>> loadAllLists() {
        return lists;
    }

    public LiveData<List<ProductEntry>> loadAllProducts() {
        return products;
    }

    public LiveData<List<ShoppingListData>> loadShoppingListData() {
        return data;
    }

    public LiveData<String> getShoppingListName(){
        return listName;
    }

    //--------------------------------------------------------------
    // DATABASE - ListEntry
    //--------------------------------------------------------------

    public static void deleteList(ListEntry list) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.shoppingListDao().deleteShoppingItemsByListId(list.getListId());
                db.listDao().deleteList(list);
            }
        });
    }

    public static void insertList(ListEntry list) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // Deactivate active list
                ListEntry activeList = db.listDao().loadActiveList();
                activeList.setIsActive(false);
                db.listDao().updateList(activeList);
                db.listDao().insertList(list);
                int listId = db.listDao().getNewestListId();
                // Add new list labels needed in ShoppingListAdapter
                List<ShoppingListEntry> labels = new ArrayList<>();
                labels.add(new ShoppingListEntry(listId, -1, 1,
                        ShoppingListAdapter.VIEW_TYPE_NEED_LIST_LABEL));
                labels.add(new ShoppingListEntry(listId, -1, 2,
                        ShoppingListAdapter.VIEW_TYPE_ALREADY_HAVE_LIST_LABEL));
                db.shoppingListDao().insertShoppingListItems(labels);
            }
        });
    }

    public static void updateList(ListEntry list) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.listDao().updateList(list);
            }
        });
    }

    public static void changeActiveList(ListEntry list) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ListEntry activeList = db.listDao().loadActiveList();
                activeList.setIsActive(false);
                db.listDao().updateList(activeList);
                list.setIsActive(true);
                db.listDao().updateList(list);
            }
        });
    }

    //--------------------------------------------------------------
    // DATABASE - ProductEntry
    //--------------------------------------------------------------

    public static void deleteProduct(ProductEntry product) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.shoppingListDao().deleteShoppingItemsByProductId(product.getProductId());
                db.productDao().deleteProduct(product);
            }
        });
    }

    public static void insertProduct(ProductEntry product, final ViewModelInsertProductCallback productsActivityCallback) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(productsActivityCallback != null){
                    List<ProductEntry> similarProducts = db.productDao().findSimilarProducts(product.getProductName());
                    if(similarProducts.size() > 0){
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                productsActivityCallback.similarProductsDetected(product, similarProducts);
                            }
                        });
                    } else {
                        db.productDao().insertProduct(product);
                    }
                } else {
                    db.productDao().insertProduct(product);
                }
            }
        });
    }

    public interface ViewModelInsertProductCallback{
        void similarProductsDetected(ProductEntry product, List<ProductEntry> similarProducts);
    }

    public static void updateProduct(ProductEntry product) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.productDao().updateProduct(product);
            }
        });
    }

    public static void addProductToList(ProductEntry product) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int activeListId = db.listDao().loadActiveList().getListId();
                int position = db.shoppingListDao().getPositionToAddNewItem(ShoppingListAdapter.VIEW_TYPE_ALREADY_HAVE_LIST_LABEL, activeListId);
                ShoppingListEntry item = new ShoppingListEntry(activeListId, product.getProductId(),
                        position, ShoppingListAdapter.VIEW_TYPE_ITEMS);
                // Change position of items before adding new item
                List<ShoppingListEntry> itemsList = db.shoppingListDao().loadShoppingListEntriesFromPosition(position);
                for (ShoppingListEntry shoppingListEntry : itemsList) {
                    shoppingListEntry.setPosition(shoppingListEntry.getPosition() + 1);
                }
                db.shoppingListDao().updateShoppingListItems(itemsList);
                // Add new item
                db.shoppingListDao().insertShoppingItem(item);
            }
        });
    }

    //--------------------------------------------------------------
    // DATABASE - ShoppingListEntry
    //--------------------------------------------------------------

    public static void deleteShoppingListItemById(int id) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.shoppingListDao().deleteShoppingItemById(id);
            }
        });
    }

    public static void changeShoppingListItemsPosition(int movedItemId, int targetItemId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<ShoppingListEntry> shoppingListEntries;
                ShoppingListEntry movedItem = db.shoppingListDao().getShoppingListItemById(movedItemId);
                int movedItemDbPosition = movedItem.getPosition();
                int targetItemDbPosition = db.shoppingListDao()
                        .getShoppingListItemById(targetItemId).getPosition();
                if (movedItemDbPosition < targetItemDbPosition) {
                    shoppingListEntries = db.shoppingListDao()
                            .loadShoppingListEntriesBetweenPositions(movedItemDbPosition + 1, targetItemDbPosition);
                    for (ShoppingListEntry shoppingListEntry : shoppingListEntries) {
                        shoppingListEntry.setPosition(shoppingListEntry.getPosition() - 1);
                    }
                } else {
                    shoppingListEntries = db.shoppingListDao()
                            .loadShoppingListEntriesBetweenPositions(targetItemDbPosition, movedItemDbPosition - 1);
                    for (ShoppingListEntry shoppingListEntry : shoppingListEntries) {
                        shoppingListEntry.setPosition(shoppingListEntry.getPosition() + 1);
                    }
                }
                movedItem.setPosition(targetItemDbPosition);
                shoppingListEntries.add(movedItem);
                db.shoppingListDao().updateShoppingListItems(shoppingListEntries);
            }
        });
    }

    //--------------------------------------------------------------
    // DATABASE - Sample Database
    //--------------------------------------------------------------

    public static void populateSampleDatabase(Context context) {
        String sampleShoppingListName = context.getString(R.string.sample_shopping_list_name);
        String sampleProduct1Name = context.getString(R.string.sample_product1_name);
        String sampleProduct2Name = context.getString(R.string.sample_product2_name);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ListEntry listEntry = new ListEntry(sampleShoppingListName, true);
                db.listDao().insertList(listEntry);

                List<ProductEntry> productEntries = new ArrayList<>();
                productEntries.add(new ProductEntry(sampleProduct1Name));
                productEntries.add(new ProductEntry(sampleProduct2Name));
                db.productDao().insertMultipleProducts(productEntries);

                List<ShoppingListEntry> listEntries = new ArrayList<>();
                listEntries.add(new ShoppingListEntry(1, -1, 1,
                        ShoppingListAdapter.VIEW_TYPE_NEED_LIST_LABEL));
                listEntries.add(new ShoppingListEntry(1, 1, 2,
                        ShoppingListAdapter.VIEW_TYPE_ITEMS));
                listEntries.add(new ShoppingListEntry(1, 2, 3,
                        ShoppingListAdapter.VIEW_TYPE_ITEMS));
                listEntries.add(new ShoppingListEntry(1, -1, 4,
                        ShoppingListAdapter.VIEW_TYPE_ALREADY_HAVE_LIST_LABEL));
                db.shoppingListDao().insertShoppingListItems(listEntries);
            }
        });
    }
}
