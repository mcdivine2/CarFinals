package com.example.carmodels;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Car> carList;
    private OnDeleteCarClickListener onDeleteCarClickListener; // Define the interface variable

    public CarAdapter(List<Car> carList, OnDeleteCarClickListener onDeleteCarClickListener) {
        this.carList = carList;
        this.onDeleteCarClickListener = onDeleteCarClickListener; // Initialize the interface variable
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview, parent, false);
        return new CarViewHolder(view, onDeleteCarClickListener); // Pass the interface listener
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.bind(car, holder.qrCodeImageView); // Pass the qrCodeImageView reference
    }


    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView carNameTextView, carModelTextView, carYearTextView, carPriceTextView;
        ImageView carImageView, qrCodeImageView;
        OnDeleteCarClickListener onDeleteCarClickListener; // Interface listener variable

        public CarViewHolder(@NonNull View itemView, OnDeleteCarClickListener onDeleteCarClickListener) {
            super(itemView);
            carNameTextView = itemView.findViewById(R.id.txtCarName);
            carModelTextView = itemView.findViewById(R.id.txtCarModel);
            carYearTextView = itemView.findViewById(R.id.txtCarYear);
            carPriceTextView = itemView.findViewById(R.id.txtCarPrice);
            carImageView = itemView.findViewById(R.id.carImg);
            qrCodeImageView = itemView.findViewById(R.id.imgQRcode);

            // Initialize the interface listener
            this.onDeleteCarClickListener = onDeleteCarClickListener;

            // Set onClickListener for the delete button
            itemView.findViewById(R.id.btnDeleteCar).setOnClickListener(v -> {
                if (onDeleteCarClickListener != null) {
                    onDeleteCarClickListener.onDeleteCarClick(getAdapterPosition());
                }
            });
        }

        public void bind(Car car, ImageView qrCodeImageView) {
            carNameTextView.setText("Name: "+car.getCarName());
            carModelTextView.setText("Brand: "+car.getCarModel());
            carYearTextView.setText("Year: "+car.getCarYear());
            carPriceTextView.setText("Price: "+car.getCarPrice());


            if (car.getCarImage() != null && !car.getCarImage().isEmpty()) {
                Picasso.get().load(car.getCarImage()).into(carImageView);
            } else {
                // Handle case where image URL is null or empty
                carImageView.setImageResource(R.drawable.model);
            }
            generateAndDisplayQRCode(car, this.qrCodeImageView);
        }
    }
    private static void generateAndDisplayQRCode(Car car, ImageView qrCodeImageView) {
        String carDetails = "Name of car: " + car.getCarName() +
                "\nCar Model: " + car.getCarModel() +
                "\nCar Year: " + car.getCarYear() +
                "\nCar Price: " + car.getCarPrice();

        // Generate QR code for carDetails
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(carDetails, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrCodeImageView.setImageBitmap(bitmap); // Set QR code bitmap to ImageView

        } catch (WriterException e) {
            e.printStackTrace();
            // Handle QR code generation failure
            qrCodeImageView.setImageResource(R.drawable.model); // Set default QR code image
        }
    }

    public interface OnDeleteCarClickListener {
        void onDeleteCarClick(int position);
    }
}
