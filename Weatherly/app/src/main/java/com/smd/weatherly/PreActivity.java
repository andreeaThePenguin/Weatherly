package com.smd.weatherly;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class PreActivity extends AppCompatActivity {

    EditText inputCity;
    Button submitCity, openLocalList;
    BroadcastReceiver broadcastReceiver;
    String city;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_CITY = "city_pref"; // city saved in shared preferences

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new
                IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre);

        broadcastReceiver = new NetworkChangeReceiver();

        inputCity = (EditText) findViewById(R.id.input_city_id);
        submitCity = (Button)findViewById(R.id.submit_city_id);
        openLocalList = (Button)findViewById(R.id.open_local_list_button_id);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        city = sharedPreferences.getString(KEY_CITY, null);

        // open MainActivity if preference already exists
        if (city != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        openLocalList.setOnClickListener(view -> {
            Intent intent;
            intent = new Intent(getApplicationContext(), LocalListActivity.class);
            startActivity(intent);
        });

        submitCity.setOnClickListener(view -> {
            // grab text on button press
            city = inputCity.getText().toString().trim();
                if (!city.equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_CITY, city);
                    editor.apply();

                    Intent intent;
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("city_value", city);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), "Preffered city saved", Toast.LENGTH_LONG).show();
                } else
                    // small pop up with "Enter a city"
                    Toast.makeText(getApplicationContext(), "Enter a city", Toast.LENGTH_LONG).show();

        });
    }
}

