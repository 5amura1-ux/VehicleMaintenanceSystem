package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.AppointmentDAO;
import com.oscars.vehiclemaintenancesystem.model.Appointment;

import java.util.Date;
import java.util.List;

public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public String scheduleAppointment(String vehicleId, String serviceId, String packageId, String mechanicId, Date appointmentDate, String notes) throws Exception {
        return appointmentDAO.insertAppointment(vehicleId, serviceId, packageId, mechanicId, appointmentDate, notes);
    }

    public void deleteAppointment(String appointmentId) throws Exception {
        appointmentDAO.deleteAppointment(appointmentId);
    }

    public Appointment getAppointmentById(String appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public List<Appointment> getAppointmentsByMechanic(String mechanicId) {
        return appointmentDAO.getAppointmentsByMechanic(mechanicId);
    }
}