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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


// ... (existing imports)

public class Dashboard extends AppCompatActivity implements CarAdapter.OnDeleteCarClickListener {

    private RecyclerView recyclerView;
    private List<Car> carList;
    private CarAdapter carAdapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Button home, userInfo, filter;

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

    private void btnInit(){
        home = findViewById(R.id.home);
        userInfo = findViewById(R.id.btnUser);
        filter = findViewById(R.id.filter); // Add filter button

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Dashboard2.class);
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

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
    }
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the car name...");

        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(Dashboard.this, "Speech recognition not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);
                    filterRecyclerView(spokenText);
                }
            }
        }
    }

    private void filterRecyclerView(String searchText) {
        List<Car> filteredList = new ArrayList<>();
        List<Car> nonMatchingList = new ArrayList<>();

        for (Car car : carList) {
            String carName = car.getCarName().toLowerCase();
            searchText = searchText.toLowerCase();

            if (carName.contains(searchText)) {
                // Partial match found, add to the list
                filteredList.add(car);
            } else {
                int distance = calculateLevenshteinDistance(carName, searchText);
                // Adjust the threshold value based on your preference
                if (distance <= 4) {
                    // Close match found, add to the list
                    filteredList.add(car);
                } else {
                    nonMatchingList.add(car);
                }
            }
        }

        // Add the non-matching cars to the filtered list
        filteredList.addAll(nonMatchingList);

        carAdapter.filterList(filteredList);
    }


    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    private int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }





}

