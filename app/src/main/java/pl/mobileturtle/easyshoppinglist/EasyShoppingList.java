package pl.mobileturtle.easyshoppinglist;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import timber.log.Timber;

public class EasyShoppingList extends Application {
    private static FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public static FirebaseAnalytics getFirebaseAnalytics(){
        return firebaseAnalytics;
    }
}
