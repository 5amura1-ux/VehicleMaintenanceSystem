package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Appointment;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface AppointmentDAO {
    void addAppointment(Appointment appointment);
    Appointment getAppointment(String appointmentId);
    List<Appointment> getAllAppointments();
    void updateAppointment(Appointment appointment);
    void deleteAppointment(String appointmentId);

    // Implementation
    class Impl implements AppointmentDAO {
        @Override
        public void addAppointment(Appointment appointment) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(appointment);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Appointment getAppointment(String appointmentId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Appointment.class, appointmentId);
            }
        }

        @Override
        public List<Appointment> getAllAppointments() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Appointment> query = session.createQuery("FROM Appointment", Appointment.class);
                return query.list();
            }
        }

        @Override
        public void updateAppointment(Appointment appointment) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(appointment);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteAppointment(String appointmentId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Appointment appointment = session.get(Appointment.class, appointmentId);
                if (appointment != null) session.delete(appointment);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}