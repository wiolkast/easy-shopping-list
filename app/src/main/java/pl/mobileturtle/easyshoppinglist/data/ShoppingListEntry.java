package pl.mobileturtle.easyshoppinglist.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_lists")
public class ShoppingListEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int listId;
    @ForeignKey(entity = ListEntry.class, parentColumns = "listId", childColumns = "listId")
    private final int productId;
    @ForeignKey(entity = ProductEntry.class, parentColumns = "productId", childColumns = "productId")
    private int position;
    private final int type;

    @Ignore
    public ShoppingListEntry(int listId, int productId, int position, int type){
        this.listId = listId;
        this.productId = productId;
        this.position = position;
        this.type = type;
    }

    public ShoppingListEntry(int id, int listId, int productId, int position, int type){
        this.id = id;
        this.listId = listId;
        this.productId = productId;
        this.position = position;
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public int getListId(){
        return this.listId;
    }

    public int getProductId(){
        return this.productId;
    }

    public int getPosition(){
        return this.position;
    }

    public int getType(){
        return this.type;
    }

    public void setPosition(int position){
        this.position = position;
    }
}