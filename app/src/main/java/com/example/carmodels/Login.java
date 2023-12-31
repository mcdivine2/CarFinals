package com.example.carmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Login extends AppCompatActivity {

    private EditText userName, password;
    private Button signUp, logIn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initClickBtn();

        mAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    private void loginUser() {
        String enteredUserName = this.userName.getText().toString();
        String enteredPassword = password.getText().toString();


        if (enteredUserName.isEmpty() || enteredPassword.isEmpty()) {
            Toast.makeText(Login.this, "Please fill in all the data", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(enteredUserName, enteredPassword)
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            // Firebase Authentication login successful
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Proceed to the home screen or desired activity upon successful login
                            Intent intent = new Intent(Login.this, Dashboard.class);
                            startActivity(intent);
                            finish(); // Finish the login activity
                        } else {
                            // Firebase Authentication login failed

                        }
                    });
        }
    }


    private void initViews() {
        userName = findViewById(R.id.txtEmail);
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