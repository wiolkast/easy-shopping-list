package pl.mobileturtle.easyshoppinglist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ListDao {
    @Query("SELECT * FROM lists")
    LiveData<List<ListEntry>> loadAllLists();

    @Query("SELECT listName FROM lists WHERE isActive = 1 LIMIT 1")
    LiveData<String> getShoppingListName();

    @Query("SELECT * FROM lists WHERE isActive = 1 LIMIT 1")
    ListEntry loadActiveList();

    @Query("SELECT MAX(listId) FROM lists")
    int getNewestListId();

    @Insert
    void insertList(ListEntry listEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateList(ListEntry listEntry);

    @Delete
    void deleteList(ListEntry listEntry);
}
