package tech.hanafi.weatherapp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tech.hanafi.weatherapp.AppConstant;

/**
 * Created by han.afi on 10/3/17.
 */

public interface WeatherService {


    Gson gson = new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();


    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging.setLevel(HttpLoggingInterceptor.Level.NONE))
            //.addInterceptor(new MyInterceptor())
            .build();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(AppConstant.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build();

    @GET("forecast/{apiKey}/{longLat}")
    Call <JsonObject> getForecast(@Path("apiKey") String apiKey, @Path("longLat") String longLat);

}
