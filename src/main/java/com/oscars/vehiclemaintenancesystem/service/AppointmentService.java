package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.controller.LoginController;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

public class AppointmentService {
    public List<Appointment> getAllAppointments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> query = session.createQuery("FROM Appointment", Appointment.class);
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

    // Removed unused 'text' parameter
    public String scheduleAppointment(String vehicleId, String serviceId, String packageId, String mechanicId, Date appointmentDate, String timeslot, String notes) {
        // Validate timeslot format (HH:MM, 24-hour clock)
        if (!timeslot.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$")) {
            throw new IllegalArgumentException("Timeslot must be in HH:MM format (24-hour clock), e.g., '09:00' or '14:30'");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection conn = session.doReturningWork(connection -> connection);

            // Call the INSERT_APPOINTMENT procedure
            CallableStatement stmt = conn.prepareCall("{call OSCARS.INSERT_APPOINTMENT(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, vehicleId);
            stmt.setString(2, serviceId);
            stmt.setString(3, packageId != null && !packageId.isEmpty() ? packageId : null); // Handle null packageId
            stmt.setString(4, mechanicId);
            stmt.setDate(5, new java.sql.Date(appointmentDate.getTime()));
            stmt.setString(6, timeslot); // Use the provided timeslot parameter
            stmt.setString(7, notes != null ? notes : ""); // Handle null notes
            stmt.registerOutParameter(8, Types.VARCHAR); // For p_appointment_id

            stmt.execute();

            String appointmentId = stmt.getString(8);
            session.getTransaction().commit();
            return appointmentId;
        } catch (Exception e) {
            throw new RuntimeException("Error creating appointment: " + e.getMessage(), e);
        }
    }

    public Appointment getAppointmentById(String appointmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Appointment.class, appointmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving appointment by ID " + appointmentId + ": " + e.getMessage());
        }
    }

    public void updateAppointment(String appointmentId, String packageId, String mechanicId, Date appointmentDate, String timeslot, String status, String notes, String invoiceGenerated) {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL UPDATE_APPOINTMENT(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, appointmentId);
            stmt.setString(2, packageId);
            stmt.setString(3, mechanicId);
            stmt.setDate(4, new java.sql.Date(appointmentDate.getTime()));
            stmt.setString(5, timeslot);
            stmt.setString(6, status);
            stmt.setString(7, notes);
            stmt.setString(8, LoginController.getLoggedInUser()); // Pass the logged-in user's ID
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating appointment: " + e.getMessage());
        }
    }
}