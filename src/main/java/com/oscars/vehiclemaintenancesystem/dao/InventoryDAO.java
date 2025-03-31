package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.InventoryItem;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class InventoryDAO {
    public String insertInventory(String itemName, int quantity, int lowStockThreshold, double unitPrice) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_INVENTORY(?,?,?,?,?)}");
            stmt.setString(1, itemName);
            stmt.setInt(2, quantity);
            stmt.setInt(3, lowStockThreshold);
            stmt.setDouble(4, unitPrice);
            stmt.registerOutParameter(5, Types.VARCHAR);
            stmt.execute();
            String itemId = stmt.getString(5);
            tx.commit();
            return itemId;
        }
    }

    public void updateInventory(String itemId, int quantity) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call UPDATE_INVENTORY(?,?)}");
            stmt.setString(1, itemId);
            stmt.setInt(2, quantity);
            stmt.execute();
            tx.commit();
        }
    }

    public void deleteInventory(String itemId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call DELETE_INVENTORY(?)}");
            stmt.setString(1, itemId);
            stmt.execute();
            tx.commit();
        }
    }

    public List<InventoryItem> getAllInventoryItems() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM InventoryItem", InventoryItem.class).list();
        }
    }
}