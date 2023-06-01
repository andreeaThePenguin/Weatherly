package com.smd.weatherly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

// this class chooses launcher activity based on condition

public class NavigatorActivity extends Activity {

    String condition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        condition = "no";
        Intent intent;

        if (condition == "yes") {
            intent = new Intent(NavigatorActivity.this, MainActivity.class);
        } else {
            intent = new Intent(NavigatorActivity.this,
                    PreActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
