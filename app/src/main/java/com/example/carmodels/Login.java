package com.example.carmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private Button signUp, logIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initClickBtn();

        mAuth = FirebaseAuth.getInstance();

    }

    private void loginUser() {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(Login.this, "Please fill in all the data", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Proceed to the home screen or desired activity upon successful login
                        Intent intent = new Intent(Login.this, AddCar.class);
                        startActivity(intent);
                        finish(); // Finish the login activity
                    } else {
                        // Login failed
                        Toast.makeText(Login.this, "Login Failed. Please check your credentials", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void initViews() {
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);

        signUp = findViewById(R.id.btnSignUp);
        logIn = findViewById(R.id.btnLogIn);
    }
    private void initClickBtn() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }
}