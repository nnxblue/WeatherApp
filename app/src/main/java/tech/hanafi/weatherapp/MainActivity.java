package tech.hanafi.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.hanafi.weatherapp.adapters.MyAdapter;
import tech.hanafi.weatherapp.model.CardWeather;
import tech.hanafi.weatherapp.model.Datum;
import tech.hanafi.weatherapp.model.Hourly;
import tech.hanafi.weatherapp.model.WeatherData;
import tech.hanafi.weatherapp.service.WeatherService;
import tech.hanafi.weatherapp.utils.PermissionUtils;
import tech.hanafi.weatherapp.utils.TrackGPS;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_weather)
    RecyclerView mRecyclerView;

    @BindView(R.id.txtTempSummary)
    TextView txtTempSummary;
    @BindView(R.id.txtCurrentSummary)
    TextView txtCurrentSummary;
    @BindView(R.id.txtHourlySummary)
    TextView txtHourlySummary;


    private TrackGPS gps;
    private WeatherService mWeatherService;
    private double latitude;
    private double longitude;
    private WeatherData mWeatherData;



    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CardWeather> weatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        weatherList = new ArrayList<>();

        //mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_weather);
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
         mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this, weatherList);
        mRecyclerView.setAdapter(mAdapter);

      //  prepareData();

        getLocation();

        getWeatherForecast();
    }

    private void getWeatherForecast() {

        mWeatherService = WeatherService.retrofit.create(WeatherService.class);
        final Call<JsonObject> call = mWeatherService.getForecast(AppConstant.API_KEY, (""+latitude+","+longitude));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Weather", response.body().toString());
                try{
                    Gson mGson = new Gson();
                    mWeatherData = mGson.fromJson(response.body(), WeatherData.class);
                    prepareData();
                }catch (Exception ex){
                    Log.d("Exception", ex.toString());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getLocation() {

        if (PermissionUtils.isLocationPermitted(this)) {

            // Simple and Basic Getting Long Lat
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (null != lastGPS) {
                    this.longitude = lastGPS.getLongitude();
                    this.latitude = lastGPS.getLatitude();
                } else if (null != lastNetwork) {
                    this.longitude = lastNetwork.getLongitude();
                    this.latitude = lastNetwork.getLatitude();
                }
            }
        }

    }

    private void prepareData(){

        txtTempSummary.setText(""+convertFahrenheitToCelcius(mWeatherData.getCurrently().getTemperature()) + (char) 0x00B0 + " C");
        txtCurrentSummary.setText(mWeatherData.getCurrently().getSummary());
        txtHourlySummary.setText(mWeatherData.getHourly().getSummary());

        CardWeather cw = new CardWeather();

        for (Datum hourData:mWeatherData.getHourly().getData()) {
            cw = new CardWeather(convertUnixTimeToLocalTime(hourData.getTime()), hourData.getSummary(), ""+convertFahrenheitToCelcius(hourData.getTemperature()));
            weatherList.add(cw);
        }

//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);
//        weatherList.add(cw);

        mAdapter.notifyDataSetChanged();



    }

    private String convertUnixTimeToLocalTime(int time){
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    private int convertFahrenheitToCelcius(double fahrenheit) {
        Double celcius = ((fahrenheit - 32) * 5 / 9);
        return celcius.intValue();
    }
}
