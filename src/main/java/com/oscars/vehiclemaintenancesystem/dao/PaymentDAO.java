package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Payment;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface PaymentDAO {
    void addPayment(Payment payment);
    Payment getPayment(String paymentId);
    List<Payment> getAllPayments();
    void updatePayment(Payment payment);
    void deletePayment(String paymentId);

    // Implementation
    class Impl implements PaymentDAO {
        @Override
        public void addPayment(Payment payment) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(payment);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Payment getPayment(String paymentId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Payment.class, paymentId);
            }
        }

        @Override
        public List<Payment> getAllPayments() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
                return query.list();
            }
        }

        @Override
        public void updatePayment(Payment payment) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(payment);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deletePayment(String paymentId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Payment payment = session.get(Payment.class, paymentId);
                if (payment != null) session.delete(payment);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}