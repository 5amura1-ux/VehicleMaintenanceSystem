package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Customer;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class CustomerDAO {
    public String insertCustomer(String firstName, String lastName, String phoneNumber, String email, String address) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_CUSTOMER(?,?,?,?,?,?)}");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, email);
            stmt.setString(5, address);
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.execute();
            String customerId = stmt.getString(6);
            tx.commit();
            return customerId;
        }
    }

    public void deleteCustomer(String customerId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call DELETE_CUSTOMER(?)}");
            stmt.setString(1, customerId);
            stmt.execute();
            tx.commit();
        }
    }

    public List<Customer> getAllCustomers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Customer", Customer.class).list();
        }
    }
}