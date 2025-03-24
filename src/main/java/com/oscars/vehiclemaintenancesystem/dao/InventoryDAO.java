package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Inventory;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface InventoryDAO {
    void addInventory(Inventory inventory);
    Inventory getInventory(String itemId);
    List<Inventory> getAllInventory();
    void updateInventory(Inventory inventory);
    void deleteInventory(String itemId);

    // Implementation
    class Impl implements InventoryDAO {
        @Override
        public void addInventory(Inventory inventory) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(inventory);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Inventory getInventory(String itemId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Inventory.class, itemId);
            }
        }

        @Override
        public List<Inventory> getAllInventory() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Inventory> query = session.createQuery("FROM Inventory", Inventory.class);
                return query.list();
            }
        }

        @Override
        public void updateInventory(Inventory inventory) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(inventory);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteInventory(String itemId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Inventory inventory = session.get(Inventory.class, itemId);
                if (inventory != null) session.delete(inventory);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}