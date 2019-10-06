package pl.mobileturtle.easyshoppinglist.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class ProductEntry {
    @PrimaryKey(autoGenerate = true)
    private int productId;
    private String productName;

    @Ignore
    public ProductEntry(String productName){
        this.productName = productName;
    }

    public ProductEntry(int productId, String productName){
        this.productId = productId;
        this.productName = productName;
    }

    public int getProductId(){
        return this.productId;
    }

    public String getProductName(){
        return this.productName;
    }

    public void setProductName(String productName){
        this.productName = productName;
    }
}
