package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.NotificationDAO;
import com.oscars.vehiclemaintenancesystem.model.Notification;

import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public List<Notification> getAllNotifications() {
        return notificationDAO.getAllNotifications();
    }

    public void deleteNotification(String notificationId) {
        notificationDAO.deleteNotification(notificationId);
    }

    public List<Notification> getNotificationsByUser(String userId) {
        return notificationDAO.getNotificationsByUser(userId);
    }
}