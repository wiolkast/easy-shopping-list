package pl.mobileturtle.easyshoppinglist.utilities;

import android.app.Application;

import pl.mobileturtle.easyshoppinglist.BuildConfig;
import timber.log.Timber;

public class EasyShoppingList extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }
}
