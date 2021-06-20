package com.pro.cs.is.whatsappstatusdownloder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.pro.cs.is.whatsappstatusdownloder.ui.MainActivity;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
