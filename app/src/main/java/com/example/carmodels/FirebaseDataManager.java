package com.example.carmodels;

import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDataManager {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;

    public FirebaseDataManager() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
    }

    // Push data to Firebase Authentication
    public void pushToFirebaseAuth(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User created successfully
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Additional actions if needed
                    } else {
                        // Failed to create user
                    }
                });
    }

    // Push data to Firestore
    public void pushToFirestore(String collection, String document, Map<String, Object> data) {
        mFirestore.collection(collection).document(document)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Data added successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to add data
                });
    }

    // Push data to Realtime Database
    public void pushToRealtimeDatabase(String node, Map<String, Object> data) {
        mDatabase.child(node).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    // Data added successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to add data
                });
    }

    // Push data to Firebase Storage
    public void pushToFirebaseStorage(Uri fileUri, String storagePath) {
        StorageReference storageRef = mStorage.getReference().child(storagePath);
        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to upload file
                });
    }
}
