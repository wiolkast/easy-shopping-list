package pl.mobileturtle.easyshoppinglist.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ListEntry.class, ProductEntry.class, ShoppingListEntry.class}, version = 1, exportSchema = false)
public abstract class ShoppingListDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "shoppinglist.db";
    private static final Object LOCK = new Object();
    private static ShoppingListDatabase instance;

    public static ShoppingListDatabase getInstance(Context context){
        if(instance==null){
            synchronized (LOCK){
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        ShoppingListDatabase.class, ShoppingListDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return instance;
    }

    public abstract ListDao listDao();
    public abstract ProductDao productDao();
    public abstract ShoppingListDao shoppingListDao();
}