package pl.devoxx4kids.devoxx4kids;

import android.app.Application;
import android.content.Context;

import com.estimote.sdk.EstimoteSDK;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import pl.devoxx4kids.devoxx4kids.model.Service;

public class MyApplication extends Application {

    private Service service;

    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), "entrop-tomek-gmail-com-s-y-ld4", "5935e8ceb0c4a4cb7d90bd45c22ee92f");
        //EstimoteSDK.enableDebugLogging(true);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    public Service getService() {
        if (service == null) {
            service = Service.Factory.create();
        }
        return service;
    }

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }
}
