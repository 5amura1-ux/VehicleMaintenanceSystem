package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Appointments", schema = "OSCARS")
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

    @Column(name = "completed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;

    @Column(name = "invoice_generated")
    private String invoiceGenerated; // This field is causing the issue

    @Column(name = "mechanic_name")
    private String mechanicName;

    @Column(name = "notes")
    private String notes;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "status")
    private String status;

    @Column(name = "timeslot")
    private String timeslot;

    @Column(name = "vehicle_make")
    private String vehicleMake;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    // Default constructor
    public Appointment() {}

    // Parameterized constructor (optional)
    public Appointment(String appointmentId, String vehicleId, String serviceId, String packageId, String mechanicId,
                       Date appointmentDate, Date completedDate, String invoiceGenerated, String mechanicName,
                       String notes, String packageName, String serviceName, String status, String timeslot,
                       String vehicleMake, String vehicleModel) {
        this.appointmentId = appointmentId;
        this.vehicleId = vehicleId;
        this.serviceId = serviceId;
        this.packageId = packageId;
        this.mechanicId = mechanicId;
        this.appointmentDate = appointmentDate;
        this.completedDate = completedDate;
        this.invoiceGenerated = invoiceGenerated;
        this.mechanicName = mechanicName;
        this.notes = notes;
        this.packageName = packageName;
        this.serviceName = serviceName;
        this.status = status;
        this.timeslot = timeslot;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
    }

    // Getters and Setters
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

    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }

    public String getInvoiceGenerated() { return invoiceGenerated; }
    public void setInvoiceGenerated(String invoiceGenerated) { this.invoiceGenerated = invoiceGenerated; }

    public String getMechanicName() { return mechanicName; }
    public void setMechanicName(String mechanicName) { this.mechanicName = mechanicName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimeslot() { return timeslot; }
    public void setTimeslot(String timeslot) { this.timeslot = timeslot; }

    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
}