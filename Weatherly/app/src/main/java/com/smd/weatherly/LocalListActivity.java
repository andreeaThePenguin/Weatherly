package com.smd.weatherly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LocalListActivity extends AppCompatActivity {

    Button localConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_list);

        localConnect = findViewById(R.id.bucharest_button_id);
        localConnect.setOnClickListener(view -> {
            Intent intent2;
            intent2 = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(intent2);
        });
    }
}