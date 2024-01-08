package com.example.carmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class AddCar extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private ImageView carImg;
    private EditText name,model,year,price;
    private Button addCar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcar);

        initViews();
        carImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        addCar.setOnClickListener(v -> createCar());

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void createCar() {
        String carName = name.getText().toString();
        String carModel = model.getText().toString();
        String carYear = year.getText().toString();
        String carPrice = price.getText().toString();

        // Check if any field is empty
        if (carName.isEmpty() || carModel.isEmpty() || carYear.isEmpty() || carPrice.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a car image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Map to store car details
        Map<String, Object> car = new HashMap<>();
        car.put("carName", carName);
        car.put("carModel", carModel);
        car.put("carYear", carYear);
        car.put("carPrice", carPrice);

        // Add car details to Firestore
        CollectionReference carsCollection = firestore.collection("cars");
        carsCollection.add(car)
                .addOnSuccessListener(documentReference -> {
                    String carId = documentReference.getId(); // Get the document ID

                    // Upload car image to Firebase Storage using the carId as a reference
                    StorageReference imageRef = storageReference.child("car_images/" + carId + ".jpg");
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                // Image uploaded successfully, now get the image URL
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();

                                    generateAndSaveQRCode(carId,carName,carModel,carYear,carPrice);

                                    // Update car document with image URL
                                    documentReference.update("carImage", imageUrl)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show();
                                                // Clear EditText fields after successful addition
                                                name.setText("");
                                                model.setText("");
                                                year.setText("");
                                                price.setText("");
                                                Intent intent = new Intent(AddCar.this, Dashboard.class);
                                                startActivity(intent);

                                                // Finish the current activity to prevent going back when pressing back button
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Failed to add car image URL", Toast.LENGTH_SHORT).show();
                                            });
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Log the error for debugging
                    Log.e("Firestore", "Failed to add car: " + e.getMessage());
                    Toast.makeText(this, "Failed to add car", Toast.LENGTH_SHORT).show();
                });
    }

    private void generateAndSaveQRCode(String carId, String carName, String carModel, String carYear, String carPrice) {
        // Generate QR code based on car details
        String carDetails = "Name of car: " + carName +
                "\nCar Model: " + carModel +
                "\nCar Year: " + carYear +
                "\nCar Price: " + carPrice;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(carDetails, BarcodeFormat.QR_CODE, 300, 300);

            // Convert BitMatrix to Bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Convert Bitmap to byte array (PNG)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bitmapData = baos.toByteArray();

            // Upload QR code image to Firebase Storage using the carId as a reference
            StorageReference qrCodeRef = storageReference.child("qr_codes/" + carId + ".png");
            qrCodeRef.putBytes(bitmapData)
                    .addOnSuccessListener(taskSnapshot -> {
                        // QR code image uploaded successfully
                        // You can add further actions if needed
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure in uploading QR code image
                        Toast.makeText(this, "Failed to upload QR code image", Toast.LENGTH_SHORT).show();
                    });

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Car Image"), PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            carImg.setImageURI(selectedImageUri); // Display the selected image in the ImageView
        }
    }
    private void initViews() {
        carImg = findViewById(R.id.imgCarProfile);

        name = findViewById(R.id.txtCarName);
        model = findViewById(R.id.txtCarModel);
        year = findViewById(R.id.txtCarYear);
        price = findViewById(R.id.txtCarPrice);

        addCar = findViewById(R.id.btnAddCar);
    }


}