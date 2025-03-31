package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Notification;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class NotificationDAO {
    public List<Notification> getAllNotifications() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Notification", Notification.class).list();
        }
    }

    public void deleteNotification(String notificationId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Notification notification = session.get(Notification.class, notificationId);
            if (notification != null) {
                session.delete(notification);
            }
            tx.commit();
        }
    }

    public List<Notification> getNotificationsByUser(String userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Notification WHERE userId = :userId", Notification.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }
}