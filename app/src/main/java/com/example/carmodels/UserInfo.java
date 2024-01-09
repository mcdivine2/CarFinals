package com.example.carmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class UserInfo extends AppCompatActivity {

    private Button btnLogOut, btnHome, btnAddCar;
    private ImageView userProfile;
    private TextView userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        btnInit();
        displayUserInfo();
    }

    private void btnInit() {
        btnLogOut = findViewById(R.id.btnLogOut);
        btnHome = findViewById(R.id.btnHome);
        btnAddCar = findViewById(R.id.btnAddCar);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Add this line to sign out the user
                Intent intent = new Intent(UserInfo.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this, Dashboard.class);
                startActivity(intent);
            }
        });
        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this, AddCar.class);
                startActivity(intent);
            }
        });
    }

    private void displayUserInfo() {
        userProfile = findViewById(R.id.imgUserProfile);
        userName = findViewById(R.id.txtUserName);
        userEmail = findViewById(R.id.txtUserEmail);

        // Retrieve and display user information
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();


            Log.d("UserInfo", "Current User UID: " + currentUser.getUid());

            // Display username and email
            userName.setText(name != null && !name.isEmpty() ? name : "No Name");
            userEmail.setText(email != null && !email.isEmpty() ? email : "No Email");

            // Load and display user profile image if available
            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                    .child("users/")
                    .child(currentUser.getUid() + "/profile.jpg");
            
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Load the image into the ImageView using Glide
                Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.baseline_refresh_24) // Placeholder image while loading
                        .error(R.drawable.model) // Image to show if loading fails
                        .into(userProfile);
            }).addOnFailureListener(exception -> {
                // Handle failed downloads
                Log.e("Firebase", "Profile image download failed: " + exception.getMessage());
                userProfile.setImageResource(R.drawable.model); // Set placeholder image
            });
        }
    }


}
