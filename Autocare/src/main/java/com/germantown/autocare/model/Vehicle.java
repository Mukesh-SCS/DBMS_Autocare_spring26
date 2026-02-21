package com.germantown.autocare.model;

import java.time.LocalDateTime;

/**
 * Data model for a vehicle (belongs to a customer).
 */
public class Vehicle {
    private int vehicleId;
    private int customerId;
    private String vin;
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Vehicle() {}

    public Vehicle(int customerId, String vin, String make, String model, int year, String licensePlate) {
        this.customerId = customerId;
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
    }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return year + " " + make + " " + model + (licensePlate != null && !licensePlate.isBlank() ? " (" + licensePlate + ")" : "");
    }
}
