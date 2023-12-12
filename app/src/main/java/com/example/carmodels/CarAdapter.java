package com.example.carmodels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Car> carList;

    public CarAdapter(List<Car> carList) {
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carview, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView carNameTextView, carModelTextView, carYearTextView, carPriceTextView;
        ImageView carImageView;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carNameTextView = itemView.findViewById(R.id.txtCarName);
            carModelTextView = itemView.findViewById(R.id.txtCarModel);
            carYearTextView = itemView.findViewById(R.id.txtCarYear);
            carPriceTextView = itemView.findViewById(R.id.txtCarPrice);
            carImageView = itemView.findViewById(R.id.carImg); // Add this line to initialize carImageView
        }

        public void bind(Car car) {
            carNameTextView.setText(car.getCarName());
            carModelTextView.setText(car.getCarModel());
            carYearTextView.setText(car.getCarYear());
            carPriceTextView.setText(car.getCarPrice());

            if (car.getCarImage() != null && !car.getCarImage().isEmpty()) {
                Picasso.get().load(car.getCarImage()).into(carImageView);
            } else {
                // Handle case where image URL is null or empty
                carImageView.setImageResource(R.drawable.model);
            }
        }
    }

}
