package tv.bppt.app.dev.api;

import tv.bppt.app.dev.data.constant.AppConstant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiUtilities {

    private static Retrofit retrofit = null;

    public static ApiInterface getApiInterface() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(AppConstant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }
}
