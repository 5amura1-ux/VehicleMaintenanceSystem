package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Error_Log")
public class ErrorLog {
    @Id
    @Column(name = "error_id")
    private String errorId;

    @Column(name = "procedure_name")
    private String procedureName;

    @Column(name = "error_code")
    private int errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Getters and setters
    public String getErrorId() { return errorId; }
    public void setErrorId(String errorId) { this.errorId = errorId; }
    public String getProcedureName() { return procedureName; }
    public void setProcedureName(String procedureName) { this.procedureName = procedureName; }
    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}