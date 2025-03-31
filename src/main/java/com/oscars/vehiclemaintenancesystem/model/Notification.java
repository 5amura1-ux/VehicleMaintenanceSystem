package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Notifications")
public class Notification {
    @Id
    @Column(name = "notification_id")
    private String notificationId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "message")
    private String message;

    @Column(name = "created_date")
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    // Getters and setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}