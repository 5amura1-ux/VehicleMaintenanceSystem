package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Appointments")
public class Appointment {
    @Id
    @Column(name = "appointment_id")
    private String appointmentId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "package_id")
    private String packageId;

    @Column(name = "mechanic_id")
    private String mechanicId;

    @Column(name = "appointment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentDate;

    @Column(name = "status")
    private String status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "invoice_generated")
    private String invoiceGenerated;

    // Getters and setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getMechanicId() { return mechanicId; }
    public void setMechanicId(String mechanicId) { this.mechanicId = mechanicId; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getInvoiceGenerated() { return invoiceGenerated; }
    public void setInvoiceGenerated(String invoiceGenerated) { this.invoiceGenerated = invoiceGenerated; }
}