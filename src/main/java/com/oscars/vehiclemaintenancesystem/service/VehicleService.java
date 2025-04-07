package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.VehicleDAO;
import com.oscars.vehiclemaintenancesystem.model.Vehicle;

import java.util.List;

public class VehicleService {
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }

    public List<Vehicle> getVehiclesByCustomer(String customerId) {
        return vehicleDAO.getVehiclesByCustomer(customerId);
    }

    public String addVehicle(String customerId, String vin, String make, String model, int year, String licensePlate, String color) {
        return vehicleDAO.addVehicle(customerId, vin, make, model, year, licensePlate, color);
    }
    public void deleteVehicle(String vehicleId) {
        vehicleDAO.deleteVehicle(vehicleId);
    }

    public void updateVehicle(String vehicleId, String make, String model, int year, String licensePlate, String color) {
        vehicleDAO.updateVehicle(vehicleId, make, model, year, licensePlate, color);
    }
}