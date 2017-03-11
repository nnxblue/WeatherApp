package tech.hanafi.weatherapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
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
import tech.hanafi.weatherapp.model.WeatherData;
import tech.hanafi.weatherapp.service.WeatherService;
import tech.hanafi.weatherapp.utils.GPSTracker;
import tech.hanafi.weatherapp.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_weather)
    RecyclerView mRecyclerView;

    @BindView(R.id.txtMainMessage)
    TextView txtMainMessage;
    @BindView(R.id.imgWeatherIcon)
    ImageView imgWeatherIcon;
    @BindView(R.id.txtTempSummary)
    TextView txtTempSummary;
    @BindView(R.id.txtCurrentSummary)
    TextView txtCurrentSummary;
    @BindView(R.id.txtHourlySummary)
    TextView txtHourlySummary;


    private GPSTracker gps;
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

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this, weatherList);
        mRecyclerView.setAdapter(mAdapter);

        //getWeatherForecast();


    }

    @Override
    protected void onResume() {
        super.onResume();

        getWeatherForecast();

    }

    /**
     * Get Weather Forecast
     */
    private void getWeatherForecast() {
        if (getLocation()) {

            mWeatherService = WeatherService.retrofit.create(WeatherService.class);
            final Call<JsonObject> call = mWeatherService.getForecast(AppConstant.API_KEY, ("" + latitude + "," + longitude));

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("Weather", response.body().toString());
                    try {
                        Gson mGson = new Gson();
                        mWeatherData = mGson.fromJson(response.body(), WeatherData.class);
                        prepareData();
                        txtMainMessage.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        txtMainMessage.setText(ex.getMessage());
                        Log.d("Exception", ex.toString());
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    txtMainMessage.setText(t.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "No Location Data Available", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get Current Location
     *
     * @return false-if unable to retrieve current location
     */
    private boolean getLocation() {

        boolean gotLongLat = false;
        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            gotLongLat = true;
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

            //stop GPS after getting longlat
            gps.stopUsingGPS();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        return gotLongLat;
    }

    private void prepareData() {

        imgWeatherIcon.setImageResource(getIconDrawable(mWeatherData.getCurrently().getIcon()));
        txtTempSummary.setText("" + convertFahrenheitToCelcius(mWeatherData.getCurrently().getTemperature()) + (char) 0x00B0 + " C");
        txtCurrentSummary.setText(mWeatherData.getCurrently().getSummary());
        txtHourlySummary.setText(mWeatherData.getHourly().getSummary());

        CardWeather cw = new CardWeather();

        for (Datum hourData : mWeatherData.getHourly().getData()) {
            int iconId = getIconDrawable(hourData.getIcon());
            cw = new CardWeather(iconId, convertUnixTimeToLocalTime(hourData.getTime()), hourData.getSummary(), "" + convertFahrenheitToCelcius(hourData.getTemperature()));
            weatherList.add(cw);
        }

        mAdapter.notifyDataSetChanged();

    }

    /**
     * @param time
     * @return converted time from unix time stamp to GMT+8 time
     */
    private String convertUnixTimeToLocalTime(int time) {
        Date date = new Date(time * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    /**
     * @param fahrenheit
     * @return celsius
     */
    private int convertFahrenheitToCelcius(double fahrenheit) {
        Double celcius = ((fahrenheit - 32) * 5 / 9);
        return celcius.intValue();
    }


    private int getIconDrawable(String name) {
        name = name.replace("-", "_");
        name = name.replace("partly_", "");
        int drawableId = -1;
        try {
            Class res = R.drawable.class;
            Field field = res.getField(name);
            drawableId = field.getInt(null);
        } catch (Exception e) {
            Log.e("MyTag", "Failure to get drawable id.", e);
        }

        return drawableId;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //prepareData();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.stopUsingGPS();
    }
}
