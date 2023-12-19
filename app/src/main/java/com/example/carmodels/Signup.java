package com.example.carmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;


public class Signup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ImageView userImg;
    private EditText userName, name,email,password,retypePassword;
    private Button signNow;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        initClickBtn();

        mAuth = FirebaseAuth.getInstance();
        userImg.setOnClickListener(v -> {
            openImageChooser(); // Open image chooser when the user clicks on the image view
        });

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    private void initViews() {
        userImg = findViewById(R.id.imgUserProfile);

        userName = findViewById(R.id.txtUserName);
        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        retypePassword = findViewById(R.id.txtRetypePassword);

        signNow = findViewById(R.id.btnSignNow);
    }

    private void createUser() {
        String userName = this.userName.getText().toString();
        String nameofUser = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userRetypePassword = retypePassword.getText().toString();

        if (userName.isEmpty() ||nameofUser.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userRetypePassword.isEmpty()) {
            Toast.makeText(Signup.this, "Please fill in all the data", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(userEmail)) {
            Toast.makeText(Signup.this, "Invalid email format", Toast.LENGTH_SHORT).show();
        } else if (!isValidPassword(userPassword)) {
            Toast.makeText(Signup.this, "Password does not meet criteria", Toast.LENGTH_SHORT).show();
        } else if (!userPassword.equals(userRetypePassword)) {
            Toast.makeText(Signup.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
        } else if (selectedImageUri == null) {
            Toast.makeText(Signup.this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> user = new HashMap<>();
            user.put("userName", userName);
            user.put("nameofUser", nameofUser);
            user.put("userEmail", userEmail);
            user.put("userPassword", userPassword);
            CollectionReference userCollection = firestore.collection("user");
            userCollection.add(user)
                    .addOnSuccessListener(documentReference -> {
                        String userID = documentReference.getId(); // Get the document ID

                        // Upload car image to Firebase Storage using the carId as a reference

                    })
                    .addOnFailureListener(e -> {
                        // Log the error for debugging
                        Log.e("Firestore", "Failed to add car: " + e.getMessage());
                        Toast.makeText(this, "Failed to add car", Toast.LENGTH_SHORT).show();
                    });

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();

                                // Create a reference to the Firebase Storage location for user's profile picture
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                                        .child("users/" + userId + "/profile.jpg"); // Adjust file name and path as needed

                                // Create UserProfileChangeRequest to include the user's name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameofUser)
                                        .build();

                                // Update the user's profile with the provided name
                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileUpdateTask -> {
                                            if (profileUpdateTask.isSuccessful()) {
                                                // Upload the selected image to Firebase Storage
                                                storageRef.putFile(selectedImageUri)
                                                        .addOnSuccessListener(taskSnapshot -> {
                                                            // Image uploaded successfully
                                                            Toast.makeText(Signup.this, "Register Success!", Toast.LENGTH_SHORT).show();

                                                            // Move intent to start the Login activity here, upon successful registration
                                                            Intent intent = new Intent(Signup.this, Login.class);
                                                            startActivity(intent);
                                                            finish(); // Finish the current activity (Signup)
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle unsuccessful image upload
                                                            Toast.makeText(Signup.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                // Profile update failed
                                                Toast.makeText(Signup.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(Signup.this, "Register Fail", Toast.LENGTH_SHORT).show();
                            // User creation failed, stay on the same activity for correction
                        }
                    });
        }
    }


    private boolean isValidPassword(String password) {
        // Regex pattern to enforce password requirements
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!_\\-(){}\\[\\]:;',.?/~*`\"\\\\]).{8,}$";
        // Explanation of the regex pattern:
        // ^                 Start of string
        // (?=.*[a-z])       At least one lowercase letter
        // (?=.*[A-Z])       At least one uppercase letter
        // (?=.*\d)          At least one digit
        // (?=.*[@#$%^&+=!_\-(){}\[\]:;',.?/~*`"\\])  At least one special character
        // .{8,}             Minimum length of the password (here, set to 8 characters)
        // $                 End of string

        return password.matches(passwordPattern);
    }
    private boolean isValidEmail(String email) {
        // Regex pattern to validate email format
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.+[a-zA-Z]{2,}";

        return email.matches(emailPattern);
    }
    private void initClickBtn() {
        signNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();

            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            userImg.setImageURI(selectedImageUri); // Display the selected image in the ImageView
        }
    }
}