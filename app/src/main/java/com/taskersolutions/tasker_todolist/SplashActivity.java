package com.taskersolutions.tasker_todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // hide action bar
        getSupportActionBar().hide();

        // direct to main activity class
        final Intent i = new Intent(SplashActivity.this, MainActivity.class);

        // handler to delay splash screen after 1000ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(i);
                finish();
            }
        }, 800);
    }
}