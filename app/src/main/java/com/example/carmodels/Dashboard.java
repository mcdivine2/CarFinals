package com.example.carmodels;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<Car> carList;
    private CarAdapter carAdapter;
    private FirebaseStorage storage; // Declare FirebaseStorage variable
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        recyclerView = findViewById(R.id.recylerViewPopulate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        carList = new ArrayList<>();
        carAdapter = new CarAdapter(carList);

        recyclerView.setAdapter(carAdapter);

        // Retrieve data from Firestore
        CollectionReference carsRef = db.collection("cars");
        carsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Task<Uri>> tasks = new ArrayList<>(); // Store download tasks

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Car car = documentSnapshot.toObject(Car.class);
                String imagePath = documentSnapshot.getString("imagePath");

                if (imagePath != null && !imagePath.isEmpty()) {
                    StorageReference imageRef = storage.getReference().child(imagePath);
                    Task<Uri> downloadTask = imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        car.setCarImage(uri.toString()); // Use setCarImage() to assign the image URL
                        carList.add(car);
                        carAdapter.notifyDataSetChanged(); // Notify adapter for each successful download
                    }).addOnFailureListener(e -> {
                        // Log the error message to identify the issue
                        Log.e("FirebaseStorageError", "Error fetching image: " + e.getMessage());

                        carList.add(car); // Add the car even if image retrieval fails
                        carAdapter.notifyDataSetChanged(); // Notify adapter for each failure
                    });

                    tasks.add(downloadTask); // Add the download task to the list
                } else {
                    carList.add(car);
                }
            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                // Notify adapter only when all download tasks are complete
                // (You may want to remove this and rely on individual notifications if needed)
                carAdapter.notifyDataSetChanged();
            });
        }).addOnFailureListener(e -> {
            // Handle failure to fetch cars
        });


    }
}
