package com.irn.vpn.activity;

import android.content.Intent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.irn.vpn.R;

public class SplashScreen extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        coordinatorLayout = findViewById(R.id.cordi);

        if (!Utility.isOnline(getApplicationContext())) {


            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "It Looks Like You Have not Connected With Internet", Snackbar.LENGTH_LONG);
            snackbar.show();


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            },1000);

        }

    }
}
