package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class PaymentDAO {
    public String processPayment(String appointmentId, String paymentMethod) {
        // Validate input parameters
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID cannot be null or empty");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }

        System.out.println("Processing payment for appointmentId: " + appointmentId + ", paymentMethod: " + paymentMethod);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection conn = session.doReturningWork(connection -> connection);

            // Call the PROCESS_PAYMENT procedure
            CallableStatement stmt = conn.prepareCall("{call OSCARS.PROCESS_PAYMENT(?, ?, ?)}");
            stmt.setString(1, appointmentId);
            stmt.setString(2, paymentMethod);
            stmt.registerOutParameter(3, Types.VARCHAR); // For p_payment_id

            stmt.execute();

            String paymentId = stmt.getString(3);
            System.out.println("Payment processed successfully, paymentId: " + paymentId);

            session.getTransaction().commit();
            return paymentId;
        } catch (Exception e) {
            throw new RuntimeException("Error processing payment: " + e.getMessage(), e);
        }
    }

    public List<Payment> getAllPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving payments: " + e.getMessage(), e);
        }
    }

    public Payment getPaymentById(String paymentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Payment.class, paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving payment by ID " + paymentId + ": " + e.getMessage(), e);
        }
    }

    public void deletePayment(String paymentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Payment payment = session.get(Payment.class, paymentId);
            if (payment != null) {
                session.delete(payment);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting payment: " + e.getMessage(), e);
        }
    }

    public void updatePayment(String paymentId, String paymentMethod, String paymentStatus) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Payment payment = session.get(Payment.class, paymentId);
            if (payment != null) {
                payment.setPaymentMethod(paymentMethod);
                payment.setPaymentStatus(paymentStatus);
                session.update(payment);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating payment: " + e.getMessage(), e);
        }
    }
}