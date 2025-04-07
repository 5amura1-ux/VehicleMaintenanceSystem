package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Error_Log", schema = "OSCARS")
public class ErrorLog {
    @Id
    @Column(name = "error_id")
    private String errorId;

    @Column(name = "procedure_name")
    private String procedureName;

    @Column(name = "error_code")
    private Integer errorCode; // This field is causing the issue

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Default constructor
    public ErrorLog() {}

    // Parameterized constructor (optional)
    public ErrorLog(String errorId, String procedureName, Integer errorCode, String errorMessage, Date timestamp) {
        this.errorId = errorId;
        this.procedureName = procedureName;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getErrorId() { return errorId; }
    public void setErrorId(String errorId) { this.errorId = errorId; }

    public String getProcedureName() { return procedureName; }
    public void setProcedureName(String procedureName) { this.procedureName = procedureName; }

    public Integer getErrorCode() { return errorCode; }
    public void setErrorCode(Integer errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}