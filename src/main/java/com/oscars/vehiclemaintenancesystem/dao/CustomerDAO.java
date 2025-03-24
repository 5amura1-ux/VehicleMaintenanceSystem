package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Customer;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface CustomerDAO {
    void addCustomer(Customer customer);
    Customer getCustomer(String customerId);
    List<Customer> getAllCustomers();
    void updateCustomer(Customer customer);
    void deleteCustomer(String customerId);

    // Implementation
    class Impl implements CustomerDAO {
        @Override
        public void addCustomer(Customer customer) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(customer);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Customer getCustomer(String customerId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Customer.class, customerId);
            }
        }

        @Override
        public List<Customer> getAllCustomers() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Customer> query = session.createQuery("FROM Customer", Customer.class);
                return query.list();
            }
        }

        @Override
        public void updateCustomer(Customer customer) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(customer);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteCustomer(String customerId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Customer customer = session.get(Customer.class, customerId);
                if (customer != null) session.delete(customer);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}