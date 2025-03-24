package com.oscars.vehiclemaintenancesystem.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Appointments")
public class Appointment {
    @Id
    @Column(name = "appointment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String appointmentId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private ServicePackage servicePackage;

    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private User mechanic;

    @Column(name = "appointment_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date appointmentDate;

    @Column(name = "completed_date")
    @Temporal(TemporalType.DATE)
    private Date completedDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "invoice_generated", nullable = false)
    private char invoiceGenerated;

    @Column(name = "notes")
    private String notes;

    public Appointment() {}

    public Appointment(Vehicle vehicle, Service service, ServicePackage servicePackage, User mechanic,
                       Date appointmentDate, Date completedDate, String status, char invoiceGenerated, String notes) {
        this.vehicle = vehicle;
        this.service = service;
        this.servicePackage = servicePackage;
        this.mechanic = mechanic;
        this.appointmentDate = appointmentDate;
        this.completedDate = completedDate;
        this.status = status;
        this.invoiceGenerated = invoiceGenerated;
        this.notes = notes;
    }

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    public ServicePackage getServicePackage() { return servicePackage; }
    public void setServicePackage(ServicePackage servicePackage) { this.servicePackage = servicePackage; }
    public User getMechanic() { return mechanic; }
    public void setMechanic(User mechanic) { this.mechanic = mechanic; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public char getInvoiceGenerated() { return invoiceGenerated; }
    public void setInvoiceGenerated(char invoiceGenerated) { this.invoiceGenerated = invoiceGenerated; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}