package com.smd.weatherly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String apiKey = "5f6f7e58abda35b7c1222e1ef8d8ca41"; // openweatherapi key
    String city = "bucharest"; // default test string
    String skyState = "SUNNY";
    TextView locationTxt, temperatureTxt, windTxt, descriptionTxt, tempMinTxt, tempMaxTxt, pressureTxt, humidityTxt;
    View view;
    Button changeLocationBtn;
    BroadcastReceiver broadcastReceiver;
    SharedPreferences sharedPreferences;
    ImageView weatherIconImg;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_CITY = "city_pref"; // city saved in shared preferences

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new
                IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadcastReceiver = new NetworkChangeReceiver();

        // grabs city inserted in PreActivity, if any
        Bundle extras = getIntent().getExtras();

        // grab city according to shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        city = sharedPreferences.getString(KEY_CITY, null);

        // if we have extras from PreActivity, grab them
        if (extras != null) // pre activity 2nd
            city = extras.getString("city_value");

        changeLocationBtn = (Button)findViewById(R.id.change_location_id);
        locationTxt = findViewById(R.id.location_id);
        temperatureTxt = findViewById(R.id.temp_value_id);
        tempMinTxt = findViewById(R.id.temp_min_value_id);
        tempMaxTxt = findViewById(R.id.temp_max_value_id);
        windTxt = findViewById(R.id.altitude_value_id);
        pressureTxt = findViewById(R.id.pressure_value_id);
        humidityTxt = findViewById(R.id.humidity_value_id);

        descriptionTxt = findViewById(R.id.description_id);
        view = (View)findViewById(R.id.main_view_id);

        // button to change location goes back to modified PreActivity
        changeLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear shared preference
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
        });

        // execute async weather task
        new weatherTask().execute();
    }

    // grab weather data and show it
    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            return HttpRequest.executeGet("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiKey);
        }

        @Override
        protected void onPostExecute(String result) {
         try {
             // define received JSON objects
             JSONObject jsonObj = new JSONObject(result);
             JSONObject sys = jsonObj.getJSONObject("sys"); // gets data for address
             JSONObject main = jsonObj.getJSONObject("main");
             JSONObject wind = jsonObj.getJSONObject("wind");
             JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);


             // get each object
             String temp_extracted = main.getString("temp");
             String windSpeed = wind.getString("speed");
             String address = jsonObj.getString("name") + ", " + sys.getString("country");
             String weatherDescription = weather.getString("description");
             String tempMin = main.getString("temp_min") + "°C";
             String tempMax = main.getString("temp_max") + "°C";
             String pressure = main.getString("pressure") + "hPa";
             String humidity = main.getString("humidity") + "%";

             // set text for objects
             temperatureTxt.setText(temp_extracted + "°C");
             windTxt.setText(windSpeed + "km/h");
             locationTxt.setText(address);
             descriptionTxt.setText(weatherDescription);
             tempMinTxt.setText(tempMin);
             tempMaxTxt.setText(tempMax);
             pressureTxt.setText(pressure);
             humidityTxt.setText(humidity);

             //skyState = "SUNNY"; // default sky state
             //if (Integer.parseInt(temp_extracted) > 15)
              //   skyState = "SUNNY";

             // change background according to state
             //switch (skyState) {
               //  case "SUNNY":
                 //    view.setBackground(getResources().getDrawable(R.drawable.sunset));
                   //  break;
                 //case "CLOUDY":
                  //   view.setBackground(getResources().getDrawable(R.drawable.sunset));
                   //  break;
             //}

         } catch (JSONException e) {
             //findViewById(R.id.loader).setVisibility(View.GONE);
             //findViewById(R.id.errorText).setVisibility(View.VISIBLE);
         }
        }
    }
}