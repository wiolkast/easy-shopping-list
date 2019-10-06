package pl.mobileturtle.easyshoppinglist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.mobileturtle.easyshoppinglist.ShoppingListAdapter;

@Dao
public interface ShoppingListDao {

    @Query("SELECT shopping_lists.id, products.productName, shopping_lists.type FROM shopping_lists LEFT JOIN products " +
            "ON shopping_lists.productId = products.productId " +
            "WHERE listId = (SELECT lists.listId FROM lists WHERE lists.isActive = 1 LIMIT 1) " +
            "ORDER BY shopping_lists.position ASC")
    LiveData<List<ShoppingListData>> loadShoppingListData();

    // Needed products for widget (conditions: active list, position bigger then
    // need_items label position (which is always equal to 1)
    // and lower then already_have_items label position of active list)
    @Query("SELECT shopping_lists.id, products.productName, shopping_lists.type FROM shopping_lists LEFT JOIN products " +
            "ON shopping_lists.productId = products.productId " +
            "WHERE listId = (SELECT lists.listId FROM lists WHERE lists.isActive = 1 LIMIT 1) " +
            "AND shopping_lists.position > 1 " +
            "AND position < (SELECT MAX(position) FROM shopping_lists " +
            "WHERE type = " + ShoppingListAdapter.VIEW_TYPE_ALREADY_HAVE_LIST_LABEL + " " +
            "AND listId = (SELECT lists.listId FROM lists WHERE lists.isActive = 1 LIMIT 1)) " +
            "ORDER BY shopping_lists.position ASC")
    List<ShoppingListData> loadWidgetNeedProducts();

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    ShoppingListEntry getShoppingListItemById(int id);

    @Query("SELECT MAX(position) FROM shopping_lists WHERE type = :type AND listId = :listId")
    int getPositionToAddNewItem(int type, int listId);

    @Query("SELECT * FROM shopping_lists WHERE position >= :position")
    List<ShoppingListEntry> loadShoppingListEntriesFromPosition(int position);

    @Query("SELECT * FROM shopping_lists WHERE position >= :fromPosition AND position <= :toPosition")
    List<ShoppingListEntry> loadShoppingListEntriesBetweenPositions(int fromPosition, int toPosition);

    @Insert
    void insertShoppingItem(ShoppingListEntry shoppingListEntry);

    @Insert
    void insertShoppingListItems(List<ShoppingListEntry> shoppingListEntries);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateShoppingListItems(List<ShoppingListEntry> shoppingListEntries);

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    void deleteShoppingItemById (int id);

    @Query("DELETE FROM shopping_lists WHERE listId = :listId")
    void deleteShoppingItemsByListId (int listId);

    @Query("DELETE FROM shopping_lists WHERE productId = :productId")
    void deleteShoppingItemsByProductId(int productId);
}