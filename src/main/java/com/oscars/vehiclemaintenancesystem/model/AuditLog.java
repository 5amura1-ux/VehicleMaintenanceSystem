package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Audit_Log")
public class AuditLog {
    @Id
    @Column(name = "log_id")
    private String logId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "action")
    private String action;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "details")
    private String details;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Getters and setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}