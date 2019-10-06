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
public interface ProductDao {
    @Query("SELECT * FROM products ORDER BY productName COLLATE NOCASE ASC")
    LiveData<List<ProductEntry>> loadAllIProducts();

    @Query("SELECT * FROM products WHERE productName LIKE '%' || :productName || '%' ORDER BY productName COLLATE NOCASE ASC")
    List<ProductEntry> findSimilarProducts(String productName);

    @Insert
    void insertProduct(ProductEntry productEntry);

    @Insert
    void insertMultipleProducts(List<ProductEntry> productEntries);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProduct(ProductEntry productEntry);

    @Delete
    void deleteProduct(ProductEntry productEntry);
}
