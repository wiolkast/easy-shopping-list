package pl.mobileturtle.easyshoppinglist.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "lists")
public class ListEntry {
    @PrimaryKey(autoGenerate = true)
    private int listId;
    private String listName;
    private Boolean isActive;

    @Ignore
    public ListEntry(String listName, Boolean isActive) {
        this.listName = listName;
        this.isActive = isActive;
    }

    public ListEntry(int listId, String listName, Boolean isActive) {
        this.listId = listId;
        this.listName = listName;
        this.isActive = isActive;
    }

    public int getListId(){
        return this.listId;
    }

    public String getListName(){
        return this.listName;
    }

    public Boolean getIsActive(){
        return this.isActive;
    }

    public void setListName(String name){
        this.listName = name;
    }

    public void setIsActive(Boolean isActive){
        this.isActive = isActive;
    }
}
