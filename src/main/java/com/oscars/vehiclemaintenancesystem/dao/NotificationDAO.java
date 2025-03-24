package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Notification;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface NotificationDAO {
    void addNotification(Notification notification);
    Notification getNotification(String notificationId);
    List<Notification> getAllNotifications();
    void updateNotification(Notification notification);
    void deleteNotification(String notificationId);

    // Implementation
    class Impl implements NotificationDAO {
        @Override
        public void addNotification(Notification notification) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(notification);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Notification getNotification(String notificationId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Notification.class, notificationId);
            }
        }

        @Override
        public List<Notification> getAllNotifications() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Notification> query = session.createQuery("FROM Notification", Notification.class);
                return query.list();
            }
        }

        @Override
        public void updateNotification(Notification notification) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(notification);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteNotification(String notificationId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Notification notification = session.get(Notification.class, notificationId);
                if (notification != null) session.delete(notification);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}