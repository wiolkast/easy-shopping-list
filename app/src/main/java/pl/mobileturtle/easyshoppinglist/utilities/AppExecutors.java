package pl.mobileturtle.easyshoppinglist.utilities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static AppExecutors instance;
    private final Executor diskIO;

    public AppExecutors(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (AppExecutors.class) {
                instance= new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return instance;
    }

    public Executor diskIO() {
        return diskIO;
    }
}