package com.oscars.vehiclemaintenancesystem.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Audit_Log")
public class AuditLog {
    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String logId;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "action", nullable = false)
    private String action;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(name = "details")
    private String details;

    public AuditLog() {}

    public AuditLog(String tableName, String action, User user, String details) {
        this.tableName = tableName;
        this.action = action;
        this.user = user;
        this.details = details;
    }

    // Getters and Setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}