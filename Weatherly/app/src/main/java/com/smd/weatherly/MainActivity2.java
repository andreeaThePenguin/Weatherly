package com.smd.weatherly;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {
    TextView locationTxt, temperatureTxt, altitudeTxt, uvTxt, tempMinTxt, tempMaxTxt, pressureTxt, humidityTxt, altitudeTagTxt;
    BroadcastReceiver broadcastReceiver;
    Float temp_val, humidity_val, pressure_val, altitude_val;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadcastReceiver = new NetworkChangeReceiver();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Event listener
        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            temp_val =
                                    snapshot.child("temperature").getValue(Float.class);
                            temperatureTxt.setText(temp_val.toString() + "°C");
                            humidity_val =
                                    snapshot.child("humidity").getValue(Float.class);
                            humidityTxt.setText(humidity_val.toString() + "%");
                            pressure_val =
                                    snapshot.child("pressure").getValue(Float.class);
                            pressureTxt.setText(pressure_val.toString() + "hPa");
                            altitude_val =
                                    snapshot.child("altitude").getValue(Float.class);
                            altitudeTxt.setText(altitude_val.toString() + "m");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        locationTxt = findViewById(R.id.location_id);
        temperatureTxt = findViewById(R.id.temp_value_id);
        tempMinTxt = findViewById(R.id.temp_min_value_id);
        tempMaxTxt = findViewById(R.id.temp_max_value_id);
        altitudeTxt = findViewById(R.id.altitude_value_id);
        altitudeTagTxt = findViewById(R.id.altitude_id);
        pressureTxt = findViewById(R.id.pressure_value_id);
        humidityTxt = findViewById(R.id.humidity_value_id);

        locationTxt.setText("Bucharest Local Station");
        altitudeTagTxt.setText("Altitude");
        tempMinTxt.setText("N/A");
        tempMaxTxt.setText("N/A");

    }
}