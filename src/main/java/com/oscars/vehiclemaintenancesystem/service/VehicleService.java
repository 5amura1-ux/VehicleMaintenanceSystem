package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.VehicleDAO;
import com.oscars.vehiclemaintenancesystem.model.Vehicle;

import java.util.List;

public class VehicleService {
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    public String addVehicle(String customerId, String vin, String make, String model, int year, String licensePlate, String color) throws Exception {
        return vehicleDAO.insertVehicle(customerId, vin, make, model, year, licensePlate, color);
    }

    public void deleteVehicle(String vehicleId) throws Exception {
        vehicleDAO.deleteVehicle(vehicleId);
    }

    public Vehicle getVehicleById(String vehicleId) {
        return vehicleDAO.getVehicleById(vehicleId);
    }

    public List<Vehicle> getVehiclesByCustomer(String customerId) {
        return vehicleDAO.getVehiclesByCustomer(customerId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }
}