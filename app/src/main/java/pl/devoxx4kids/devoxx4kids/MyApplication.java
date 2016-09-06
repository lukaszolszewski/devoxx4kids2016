package pl.devoxx4kids.devoxx4kids;

import android.app.Application;
import android.content.Context;

import com.estimote.sdk.EstimoteSDK;

import pl.devoxx4kids.devoxx4kids.model.Service;

public class MyApplication extends Application {

    private Service service;

    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), "entrop-tomek-gmail-com-s-y-ld4", "5935e8ceb0c4a4cb7d90bd45c22ee92f");
        //EstimoteSDK.enableDebugLogging(true);
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
