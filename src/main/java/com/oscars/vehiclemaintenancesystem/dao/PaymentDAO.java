package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Payment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class PaymentDAO {
    public String insertPayment(String appointmentId, String paymentMethod) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_INVOICE(?,?,?)}");
            stmt.setString(1, appointmentId);
            stmt.setString(2, paymentMethod);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();
            String paymentId = stmt.getString(3);
            tx.commit();
            return paymentId;
        }
    }

    public void deletePayment(String paymentId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call DELETE_INVOICE(?)}");
            stmt.setString(1, paymentId);
            stmt.execute();
            tx.commit();
        }
    }

    public List<Payment> getAllPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment", Payment.class).list();
        }
    }
}