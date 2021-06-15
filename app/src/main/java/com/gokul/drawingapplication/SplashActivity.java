package com.gokul.drawingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashScreen();
    }
    private void splashScreen() {
        try{
            new Handler(Looper.getMainLooper()).postDelayed(this::goToNext, 3000);
        }

        catch(Exception e){
            Log.e("Splash Screen", ""+e);
        }
    }

    private void goToNext() {
        startActivity(new Intent(this, MainActivity.class));
    }
}