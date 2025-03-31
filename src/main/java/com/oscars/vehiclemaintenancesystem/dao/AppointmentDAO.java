package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.List;

public class AppointmentDAO {
    public String insertAppointment(String vehicleId, String serviceId, String packageId, String mechanicId, Date appointmentDate, String notes) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_APPOINTMENT(?,?,?,?,?,?,?)}");
            stmt.setString(1, vehicleId);
            stmt.setString(2, serviceId);
            stmt.setString(3, packageId);
            stmt.setString(4, mechanicId);
            stmt.setDate(5, new java.sql.Date(appointmentDate.getTime()));
            stmt.setString(6, notes);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.execute();
            String appointmentId = stmt.getString(7);
            tx.commit();
            return appointmentId;
        }
    }

    public void deleteAppointment(String appointmentId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call DELETE_APPOINTMENT_SERVICE(?)}");
            stmt.setString(1, appointmentId);
            stmt.execute();
            tx.commit();
        }
    }

    public Appointment getAppointmentById(String appointmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Appointment.class, appointmentId);
        }
    }

    public List<Appointment> getAllAppointments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Appointment", Appointment.class).list();
        }
    }

    public List<Appointment> getAppointmentsByMechanic(String mechanicId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Appointment WHERE mechanicId = :mechanicId", Appointment.class)
                    .setParameter("mechanicId", mechanicId)
                    .list();
        }
    }
}