package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Vehicle;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface VehicleDAO {
    void addVehicle(Vehicle vehicle);
    Vehicle getVehicle(String vehicleId);
    List<Vehicle> getAllVehicles();
    void updateVehicle(Vehicle vehicle);
    void deleteVehicle(String vehicleId);

    // Implementation
    class Impl implements VehicleDAO {
        @Override
        public void addVehicle(Vehicle vehicle) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(vehicle);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Vehicle getVehicle(String vehicleId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Vehicle.class, vehicleId);
            }
        }

        @Override
        public List<Vehicle> getAllVehicles() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Vehicle> query = session.createQuery("FROM Vehicle", Vehicle.class);
                return query.list();
            }
        }

        @Override
        public void updateVehicle(Vehicle vehicle) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(vehicle);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteVehicle(String vehicleId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Vehicle vehicle = session.get(Vehicle.class, vehicleId);
                if (vehicle != null) session.delete(vehicle);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}