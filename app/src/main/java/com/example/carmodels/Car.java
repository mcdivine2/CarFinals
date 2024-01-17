package com.example.carmodels;

public class Car {
    private String carName;
    private String carModel;
    private String carYear;
    private String carPrice;
    private String carImage;



    private String imagePath; // Adding imagePath field
    private String id;


    // Empty constructor (required for Firestore)
    public Car() {}
    public Car(String carName, String carModel, String carYear, String carPrice, String carImage) {
        this.carName = carName;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carPrice = carPrice;
        this.carImage = carImage;
    }

    public Car(String carName, String carModel, String carYear, String carPrice) {
        this.carName = carName;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carPrice = carPrice;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(String carPrice) {
        this.carPrice = carPrice;
    }

}

