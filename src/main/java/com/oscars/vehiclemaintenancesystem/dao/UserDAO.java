package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.User;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface UserDAO {
    void addUser(User user);
    User getUser(String userId);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(String userId);

    // Implementation
    class Impl implements UserDAO {
        @Override
        public void addUser(User user) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public User getUser(String userId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(User.class, userId);
            }
        }

        @Override
        public List<User> getAllUsers() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<User> query = session.createQuery("FROM User", User.class);
                return query.list();
            }
        }

        @Override
        public void updateUser(User user) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteUser(String userId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                User user = session.get(User.class, userId);
                if (user != null) session.delete(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}