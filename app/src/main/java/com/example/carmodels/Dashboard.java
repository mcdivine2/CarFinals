package com.example.carmodels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

// ... (existing imports)

public class Dashboard extends AppCompatActivity implements CarAdapter.OnDeleteCarClickListener {

    private RecyclerView recyclerView;
    private List<Car> carList;
    private CarAdapter carAdapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Button addCar, userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recylerViewPopulate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        carList = new ArrayList<>();
        carAdapter = new CarAdapter(carList, this); // Pass 'this' to listen for delete events
        recyclerView.setAdapter(carAdapter);

        loadCarsFromFirestore();

        btnInit();



    }

    private void loadCarsFromFirestore() {
        CollectionReference carsRef = db.collection("cars");
        carsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Car car = documentSnapshot.toObject(Car.class);

                // Assuming the Car class has 'imagePath' and 'id' properties
                car.setImagePath(documentSnapshot.getString("imagePath"));
                car.setId(documentSnapshot.getId());

                carList.add(car);
            }
            carAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle failure to fetch cars
        });
    }

    public void onDeleteCarClick(int position) {
        Car carToDelete = carList.get(position);
        String carImagePath = carToDelete.getCarImage();
        String carId = carToDelete.getId();

        // Delete car image (if exists)
        if (carImagePath != null && !carImagePath.isEmpty()) {
            StorageReference imageRef = storage.getReferenceFromUrl(carImagePath);
            imageRef.delete().addOnSuccessListener(aVoid -> {
                db.collection("cars").document(carId).delete()
                        .addOnSuccessListener(aVoid1 -> {
                            carList.remove(position);
                            carAdapter.notifyItemRemoved(position);
                            carAdapter.notifyItemRangeChanged(position, carList.size());
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure to delete car data from Firestore
                            Log.e("FirestoreError", "Error deleting car: " + e.getMessage());
                        });
            }).addOnFailureListener(e -> {
                // Handle failure to delete image from Firebase Storage
                Log.e("FirebaseStorageError", "Error deleting image: " + e.getMessage());
            });
        } else {
            db.collection("cars").document(carId).delete()
                    .addOnSuccessListener(aVoid -> {
                        carList.remove(position);
                        carAdapter.notifyItemRemoved(position);
                        carAdapter.notifyItemRangeChanged(position, carList.size());
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to delete car data from Firestore
                        Log.e("FirestoreError", "Error deleting car: " + e.getMessage());
                    });
        }
    }

    private  void btnInit(){
        addCar = findViewById(R.id.btnAddCar);
        userInfo = findViewById(R.id.btnUser);

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, AddCar.class);
                startActivity(intent);

            }
        });
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, UserInfo.class);
                startActivity(intent);

            }
        });
    }

}

