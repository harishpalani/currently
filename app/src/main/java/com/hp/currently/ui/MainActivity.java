package com.hp.currently.ui;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hp.currently.R;
import com.hp.currently.model.weather.Currently;
import com.hp.currently.model.weather.Daily;
import com.hp.currently.model.weather.Forecast;
import com.hp.currently.model.weather.Hourly;
import com.hp.currently.ui.messages.AlertDialogFragment;
import com.hp.currently.ui.messages.NetworkDialogFragment;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Constants
    // Tags
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG_CONNECTIVITY_ERROR_DIALOG = "connectivity_error_dialog";
    public static final String TAG_ERROR_DIALOG = "error_dialog";

    public static final String DAILY_FORECAST = "Daily Forecast";
    public static final String HOURLY_FORECAST = "Hourly Forecast";
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Permissions
    public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;
    // public static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 0;

    // Toggles
    // metric
    public static final String IMPERIAL_UNITS = "imperial";
    public static final String METRIC_UNITS = "metric";

    // UI elements
    @Bind(R.id.temperatureTextView)
    TextView mTemperatureLabel;
    @Bind(R.id.timeTextView)
    TextView mTimeLabel;
    @Bind(R.id.locationTextView)
    TextView mLocationLabel;
    @Bind(R.id.iconImageView)
    ImageView mIconImageView;
    @Bind(R.id.humidityValue)
    TextView mHumidityValue;
    @Bind(R.id.rainValue)
    TextView mRainValue;
    @Bind(R.id.conditionTextView)
    TextView mConditionLabel;
    @Bind(R.id.relativeLayout)
    RelativeLayout mRelativeLayout;
    @Bind(R.id.swipeToRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.refreshImageView)
    ImageView mRefreshButton;
    @Bind(R.id.refreshTextView)
    TextView mRefreshTextView;
    @Bind(R.id.refreshProgressBar)
    ProgressBar mRefreshProgressBar;
    @Bind(R.id.dailyForecastButton)
    Button mDailyForecastButton;
    @Bind(R.id.hourlyForecastButton)
    Button mHourlyForecastButton;
    // fields
    private boolean mNetworkAvailable = false;
    private Forecast mForecast;
    private ColorWheel mColorWheel;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SharedPreferences mPreferences;
    private double latitude;
    private double longitude;
    private String units = IMPERIAL_UNITS; // imperial VS metric
    private Location mLocation;
    private Currently mCurrently;


    // on____() methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toggleRefresh();
        mRefreshProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10 * 1000).setFastestInterval(1 * 1000);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                toggleRefresh();
                getForecast(latitude, longitude);
                setLocation(latitude, longitude);
                toggleRefresh();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    // Location tools

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        getForecast(latitude, longitude);
        setLocation(latitude, longitude);
    }


    // Forecast information tools

    private void getForecast(double latitude, double longitude) {
        String apiKey = "f7c492c3056d5d8ce53c9a6060b0a225";
        String forecastURL = "https://api.forecast.io/forecast/" +
                apiKey + "/" +
                latitude + "," +
                longitude;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            alertUserAboutNetworkError();
        }
    }

    private void setLocation(double latitude, double longitude) {
        String apiKey = "c937f23f3198d3e2186cd0db78eb9070";
        String forecastURL = "http://api.openweathermap.org/data/2.5/weather?"
                + "lat=" + latitude + "&lon=" + longitude
                + "&units=" + units
                + "&APPID=" + apiKey;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL)
                                                   .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final String city = getCity(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLocationLabel.setText(city);
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            alertUserAboutNetworkError();
        }
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrently(getCurrentDetails(jsonData));
        forecast.setHourly(getHourlyDetails(jsonData));
        forecast.setDaily(getDailyDetails(jsonData));

        return forecast;
    }

    private String getCity(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);

        String city = forecast.getString("name");
        return city;
    }

    private Currently getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");
        Currently currentWeather = new Currently();

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTimezone(forecast.getString("timezone"));
        currentWeather.setFormattedTime(currentWeather.getFormattedTime());
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setChanceOfPrecipitation(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));

        return currentWeather;
    }

    private Currently getCurrentLocation(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);

        Currently currentWeather = new Currently();
        currentWeather.setLocation("name");

        return currentWeather;
    }

    private Hourly[] getHourlyDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray hourlyData = hourly.getJSONArray("data");
        Hourly[] hourlyForecast = new Hourly[hourlyData.length()];

        for (int i = 0; i < hourlyData.length(); i++) {
            JSONObject jsonHour = hourlyData.getJSONObject(i);
            Hourly hour = new Hourly();

            hour.setIcon(jsonHour.getString("icon"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(forecast.getString("timezone"));
            hour.setFormattedTime(hour.getFormattedTime());

            hourlyForecast[i] = hour;
        }

        return hourlyForecast;
    }

    private Daily[] getDailyDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray dailyData = daily.getJSONArray("data");
        Daily[] dailyForecast = new Daily[dailyData.length()];

        for (int i = 0; i < dailyData.length(); i++) {
            JSONObject jsonDay = dailyData.getJSONObject(i);
            Daily day = new Daily();

            day.setIcon(jsonDay.getString("icon"));
            day.setSummary(jsonDay.getString("summary"));
            day.setMaxTemperature(jsonDay.getDouble("temperatureMax"));
            day.setMinTemperature(jsonDay.getDouble("temperatureMin"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(forecast.getString("timezone"));
            day.setFormattedTime(day.getFormattedTime());

            dailyForecast[i] = day;
        }

        return dailyForecast;
    }


    // UI tools

    private void updateDisplay() {
        mTemperatureLabel.setText(mForecast.getCurrently().getTemperature() + "");
        mTimeLabel.setText("At " + mForecast.getCurrently().getFormattedTime() + " it is");
        mHumidityValue.setText(mForecast.getCurrently().getHumidity() + "");
        mRainValue.setText(mForecast.getCurrently().getChanceOfPrecipitation() + "%");
        mConditionLabel.setText(mForecast.getCurrently().getSummary());
        mIconImageView.setImageResource(mForecast.getCurrently().getIconID());
    }

    private void toggleRefresh() {
        if (mRefreshProgressBar.getVisibility() == View.INVISIBLE) {
            mRefreshProgressBar.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.INVISIBLE);
        } else {
            mRefreshProgressBar.setVisibility(View.INVISIBLE);
            mRefreshButton.setVisibility(View.VISIBLE);
        }
    }


    // Error dialogs

    private void alertUserAboutError() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(getFragmentManager(), TAG_ERROR_DIALOG);
    }

    private void alertUserAboutNetworkError() {
        NetworkDialogFragment networkDialogFragment = new NetworkDialogFragment();
        networkDialogFragment.show(getFragmentManager(), TAG_CONNECTIVITY_ERROR_DIALOG);
    }


    // Boolean methods

    private boolean isFirstTime() {
        boolean firstTime = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstTime", true);
        if (firstTime) {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstTime", false)
                    .apply();
            return true;
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkState = connectivityManager.getActiveNetworkInfo();

        if (networkState != null && networkState.isAvailable()) {
            mNetworkAvailable = true;
        }
        return mNetworkAvailable;
    }


    // OnClickListeners

    @OnClick(R.id.refreshImageView)
    public void setRefreshButton(View view) {
        getForecast(latitude, longitude);
        setLocation(latitude, longitude);
    }

    @OnClick(R.id.refreshTextView)
    public void setRefreshTextView(View view) {
        toggleRefresh();
        getForecast(latitude, longitude);
        setLocation(latitude, longitude);
        toggleRefresh();
    }

    @OnClick(R.id.dailyForecastButton)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(MainActivity.this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getDaily());
        startActivity(intent);
    }

    @OnClick(R.id.hourlyForecastButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(MainActivity.this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourly());
        startActivity(intent);
    }
}