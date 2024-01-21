package com.example.carmodels;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Check internet connectivity
        if (NetworkUtils.isNetworkAvailable(this)) {
            new Handler().postDelayed(() -> {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is already signed in, redirect to HomeActivity
                    startActivity(new Intent(MainActivity.this, Dashboard2.class));
                    finish();
                } else {
                    // No user signed in, redirect to Login activity
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }, SPLASH_DELAY);
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
