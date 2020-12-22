package com.example.sinuelo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoginActivity();
            }
        }, 3000);
    }

    private void showLoginActivity(){
        Intent intent = new Intent(
                LauncherActivity.this, LoginActivity.class
        );
        startActivity(intent);
        finish();
    }
}