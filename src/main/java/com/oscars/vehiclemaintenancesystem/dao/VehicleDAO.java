package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.controller.LoginController;
import com.oscars.vehiclemaintenancesystem.model.Vehicle;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class VehicleDAO {
    public List<Vehicle> getAllVehicles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vehicle> query = session.createQuery("FROM Vehicle", Vehicle.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving vehicles: " + e.getMessage(), e);
        }
    }

    public List<Vehicle> getVehiclesByCustomer(String customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vehicle> query = session.createQuery("FROM Vehicle WHERE customerId = :customerId", Vehicle.class);
            query.setParameter("customerId", customerId);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving vehicles for customer " + customerId + ": " + e.getMessage(), e);
        }
    }

    public void deleteVehicle(String vehicleId) {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL DELETE_VEHICLE(?)}");
            stmt.setString(1, vehicleId);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting vehicle: " + e.getMessage(), e);
        }
    }

    public String addVehicle(String customerId, String vin, String make, String model, int year, String licensePlate, String color) {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL INSERT_VEHICLE(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, customerId);
            stmt.setString(2, vin);
            stmt.setString(3, make);
            stmt.setString(4, model);
            stmt.setInt(5, year);
            stmt.setString(6, licensePlate);
            stmt.setString(7, color);
            stmt.registerOutParameter(8, Types.VARCHAR);
            stmt.execute();
            return stmt.getString(8);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding vehicle: " + e.getMessage(), e);
        }
    }

    public void updateVehicle(String vehicleId, String make, String model, int year, String licensePlate, String color) {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL UPDATE_VEHICLE(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, vehicleId);
            stmt.setString(2, make);
            stmt.setString(3, model);
            stmt.setInt(4, year);
            stmt.setString(5, licensePlate);
            stmt.setString(6, color);
            stmt.setString(7, LoginController.getLoggedInUser()); // Pass the logged-in user's ID
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating vehicle: " + e.getMessage(), e);
        }
    }
}