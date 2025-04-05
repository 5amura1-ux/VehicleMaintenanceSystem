package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.AppointmentDAO;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public Appointment getAppointmentById(String appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }
    public List<Appointment> getAllAppointments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createNamedQuery("Appointment.findAllFromView", Appointment.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving appointments: " + e.getMessage());
        }
    }

    public List<Appointment> getAppointmentsByMechanic(String mechanicId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery("FROM Appointment WHERE mechanicId = :mechanicId", Appointment.class);
            query.setParameter("mechanicId", mechanicId);
            List<Appointment> appointments = query.list();
            System.out.println("Fetched appointments for mechanic " + mechanicId + ": " + appointments.size()); // Debug log
            return appointments;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving appointments for mechanic " + mechanicId + ": " + e.getMessage());
        }
    }

    public String scheduleAppointment(String vehicleId, String serviceId, String packageId, String mechanicId, Date appointmentDate, String timeslot, String notes) {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL INSERT_APPOINTMENT(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, com.oscars.vehiclemaintenancesystem.controller.LoginController.getLoggedInUser()); // Now returns user_id
            stmt.setString(2, vehicleId); // p_vehicle_id
            stmt.setString(3, serviceId); // p_service_id
            stmt.setString(4, packageId); // p_package_id
            stmt.setString(5, mechanicId); // p_mechanic_id
            stmt.setDate(6, new java.sql.Date(appointmentDate.getTime())); // p_appointment_date
            stmt.setString(7, timeslot); // p_timeslot
            stmt.setString(8, notes); // p_notes
            stmt.registerOutParameter(9, Types.VARCHAR); // p_appointment_id
            stmt.execute();
            return stmt.getString(9);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error scheduling appointment: " + e.getMessage());
        }
    }
}