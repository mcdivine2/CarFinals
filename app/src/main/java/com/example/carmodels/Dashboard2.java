package com.example.carmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Dashboard2 extends AppCompatActivity {
    private Button addItem, dashboard, user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);

        initBtn();
        initClickables();
    }

    private void initBtn(){
        addItem = findViewById(R.id.btnAddItem);
        dashboard = findViewById(R.id.dashboard);
        user = findViewById(R.id.btnUser);
    }

    private void initClickables(){
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard2.this, AddCar.class);
                startActivity(intent);
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard2.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard2.this, UserInfo.class);
                startActivity(intent);
            }
        });
    }
}