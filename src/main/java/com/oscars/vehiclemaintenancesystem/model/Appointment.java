package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Appointments")
@NamedNativeQuery(
        name = "Appointment.findAllFromView",
        query = "SELECT * FROM VW_APPOINTMENT_HISTORY",
        resultClass = Appointment.class
)
public class Appointment {
    @Id
    @Column(name = "appointment_id")
    private String appointmentId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "vehicle_make")
    private String vehicleMake; // From view

    @Column(name = "vehicle_model")
    private String vehicleModel; // From view

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "service_name")
    private String serviceName; // From view

    @Column(name = "package_id")
    private String packageId;

    @Column(name = "package_name")
    private String packageName; // From view

    @Column(name = "mechanic_id")
    private String mechanicId;

    @Column(name = "mechanic_name")
    private String mechanicName; // From view

    @Column(name = "appointment_date")
    private Date appointmentDate;

    @Column(name = "timeslot")
    private String timeslot;

    @Column(name = "completed_date")
    private Date completedDate;

    @Column(name = "status")
    private String status;

    @Column(name = "invoice_generated")
    private String invoiceGenerated;

    @Column(name = "notes")
    private String notes;

    // Default constructor
    public Appointment() {}

    // Getters and Setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvoiceGenerated() {
        return invoiceGenerated;
    }

    public void setInvoiceGenerated(String invoiceGenerated) {
        this.invoiceGenerated = invoiceGenerated;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}