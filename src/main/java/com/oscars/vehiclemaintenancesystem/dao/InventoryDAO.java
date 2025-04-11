package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.controller.LoginController;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.InventoryItem;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class InventoryDAO {
    public String insertInventory(String itemName, int quantity, int lowStockThreshold, double unitPrice) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        String itemId = null;
        try {
            tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_INVENTORY(?,?,?,?,?,?)}");
            stmt.setString(1, itemName);
            stmt.setInt(2, quantity);
            stmt.setInt(3, lowStockThreshold);
            stmt.setDouble(4, unitPrice);
            stmt.setString(5, LoginController.getLoggedInUser()); // Pass the logged-in user's ID
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.execute();
            itemId = stmt.getString(6);
            tx.commit();

            // Refresh the session to ensure it sees the new data
            session.clear(); // Clear the session cache to force a reload from the database
        } catch (SQLException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error adding inventory item: " + e.getMessage(), e);
        } finally {
            session.close();
        }
        return itemId;
    }

    public List<InventoryItem> getAllInventoryItems() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<InventoryItem> query = session.createQuery("FROM InventoryItem", InventoryItem.class);
            List<InventoryItem> items = query.list();
            System.out.println("Retrieved " + items.size() + " inventory items");
            if (items.isEmpty()) {
                System.out.println("No items found in Inventory_Items table. Query executed: " + query.getQueryString());
            } else {
                System.out.println("Items retrieved: ");
                for (InventoryItem item : items) {
                    System.out.println(" - " + item.getItemId() + ": " + item.getItemName() + ", Quantity: " + item.getQuantity());
                }
            }
            return items;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving inventory items: " + e.getMessage(), e);
        }
    }



    public void updateInventoryItem(String itemId, int quantity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions()
                    .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                    .getConnection();
            CallableStatement stmt = conn.prepareCall("{CALL UPDATE_INVENTORY(?,?,?)}");
            stmt.setString(1, itemId);
            stmt.setInt(2, quantity);
            stmt.setString(3, LoginController.getLoggedInUser());
            stmt.execute();
            tx.commit();

            // Refresh the session to ensure it sees the updated data
            session.clear(); // Clear the session cache to force a reload from the database
        } catch (SQLException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error updating inventory item: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    public void deleteInventory(String itemId) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call DELETE_INVENTORY(?,?)}");
            stmt.setString(1, itemId);
            stmt.setString(2, LoginController.getLoggedInUser());
            stmt.execute();
            tx.commit();

            // Refresh the session to ensure it sees the updated data
            session.clear(); // Clear the session cache to force a reload from the database
        } catch (SQLException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error deleting inventory item: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}