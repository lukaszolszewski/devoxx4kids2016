package pl.devoxx4kids.devoxx4kids.model;

import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface Service {

    @GET("lukaszolszewski/devoxx4kids2016/master/service.json")
    Call<List<BeaconContent>> getBeaconContents();

    class Factory {
        public static Service create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://raw.githubusercontent.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(Service.class);
        }
    }
}
