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

    private EditText  userName, password;
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
            // Determine the chosen login method (Firestore or Firebase Auth)
            boolean useFirebaseAuth = true; // Set this based on user selection

            if (useFirebaseAuth) {
                // Attempt Firebase Authentication login
                firebaseAuthLogin(enteredUserName, enteredPassword);
            } else {
                // Attempt Firestore-based login
                firestoreLogin(enteredUserName, enteredPassword);
            }
        }
    }

    private void firebaseAuthLogin(String enteredUserName, String enteredPassword) {
        // Firebase Authentication instance
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
                        firestoreLogin(enteredUserName, enteredPassword);
                    }
                });
    }

    private void firestoreLogin(String enteredUserName, String enteredPassword) {
        // Reference to Firestore collection "user"
        CollectionReference userCollection = FirebaseFirestore.getInstance().collection("user");

        userCollection.whereEqualTo("userName", enteredUserName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Retrieve the user document and compare passwords
                        String storedPassword = document.getString("userPassword");
                        if (storedPassword != null && storedPassword.equals(enteredPassword)) {
                            // Passwords match, Firestore-based login successful
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Get the user's email from Firestore
                            String userEmail = document.getString("userEmail");

                            // Sign in with Firebase Authentication using the retrieved email and password
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, enteredPassword)
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            // Firebase Authentication login successful
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                            // Proceed to the home screen or desired activity upon successful login
                                            Intent intent = new Intent(Login.this, Dashboard.class);
                                            startActivity(intent);
                                            finish(); // Finish the login activity
                                        } else {
                                            // Firebase Authentication login failed
                                            Toast.makeText(Login.this, "Firebase Authentication Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            return; // Exit loop as the user is found
                        }
                    }
                    // No matching user found or incorrect password
                    Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error handling for the Firestore query
                    Toast.makeText(Login.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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