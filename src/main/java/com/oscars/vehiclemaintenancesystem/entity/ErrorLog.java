package com.oscars.vehiclemaintenancesystem.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Error_Log")
public class ErrorLog {
    @Id
    @Column(name = "error_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String errorId;

    @Column(name = "procedure_name", nullable = false)
    private String procedureName;

    @Column(name = "error_code", nullable = false)
    private String errorCode;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public ErrorLog() {}

    public ErrorLog(String procedureName, String errorCode, String errorMessage) {
        this.procedureName = procedureName;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // Getters and Setters
    public String getErrorId() { return errorId; }
    public void setErrorId(String errorId) { this.errorId = errorId; }
    public String getProcedureName() { return procedureName; }
    public void setProcedureName(String procedureName) { this.procedureName = procedureName; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}