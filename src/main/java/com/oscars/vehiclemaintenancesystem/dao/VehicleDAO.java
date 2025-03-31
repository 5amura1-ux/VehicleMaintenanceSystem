package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class VehicleDAO {
    public String insertVehicle(String customerId, String vin, String make, String model, int year, String licensePlate, String color) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_VEHICLE(?,?,?,?,?,?,?,?)}");
            stmt.setString(1, customerId);
            stmt.setString(2, vin);
            stmt.setString(3, make);
            stmt.setString(4, model);
            stmt.setInt(5, year);
            stmt.setString(6, licensePlate);
            stmt.setString(7, color);
            stmt.registerOutParameter(8, Types.VARCHAR);
            stmt.execute();
            String vehicleId = stmt.getString(8);
            tx.commit();
            return vehicleId;
        }
    }

    public void deleteVehicle(String vehicleId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call DELETE_VEHICLE(?)}");
            stmt.setString(1, vehicleId);
            stmt.execute();
            tx.commit();
        }
    }

    public Vehicle getVehicleById(String vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Vehicle.class, vehicleId);
        }
    }

    public List<Vehicle> getVehiclesByCustomer(String customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Vehicle WHERE customerId = :customerId", Vehicle.class)
                    .setParameter("customerId", customerId)
                    .list();
        }
    }

    public List<Vehicle> getAllVehicles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Vehicle", Vehicle.class).list();
        }
    }
}