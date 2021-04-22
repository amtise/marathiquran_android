package marathiquran.marathiquranapp;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;


public class MyApplication extends Application {

    public static Context context;

    public void onCreate() {
        super.onCreate();

        MyApplication.context = getApplicationContext();


    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
