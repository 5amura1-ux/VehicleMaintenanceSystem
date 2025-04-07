package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.controller.LoginController;
import com.oscars.vehiclemaintenancesystem.model.Customer;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CustomerDAO {
    public String insertCustomer(String firstName, String lastName, String phoneNumber, String email, String address) throws Exception {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL INSERT_CUSTOMER(?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, email);
            stmt.setString(5, address);
            stmt.registerOutParameter(6, java.sql.Types.VARCHAR);
            stmt.execute();
            return stmt.getString(6);
        } catch (SQLException e) {
            throw new Exception("Error inserting customer: " + e.getMessage(), e);
        }
    }

    public void deleteCustomer(String customerId) throws Exception {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL DELETE_CUSTOMER(?)}");
            stmt.setString(1, customerId);
            stmt.execute();
        } catch (SQLException e) {
            throw new Exception("Error deleting customer: " + e.getMessage(), e);
        }
    }

    public List<Customer> getAllCustomers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery("FROM Customer", Customer.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving customers: " + e.getMessage(), e);
        }
    }

    public void updateCustomer(String customerId, String firstName, String lastName, String phoneNumber, String email, String address) throws Exception {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL UPDATE_CUSTOMER(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, customerId);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);
            stmt.setString(5, email);
            stmt.setString(6, address);
            stmt.setString(7, LoginController.getLoggedInUser()); // Pass the logged-in user's ID
            stmt.execute();
        } catch (SQLException e) {
            throw new Exception("Error updating customer: " + e.getMessage(), e);
        }
    }
}