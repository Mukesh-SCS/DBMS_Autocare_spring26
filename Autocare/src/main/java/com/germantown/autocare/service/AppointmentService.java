package com.germantown.autocare.service;

import com.germantown.autocare.dao.AppointmentDAO;
import com.germantown.autocare.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for appointment-related operations.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public Appointment scheduleAppointment(int customerId, int vehicleId, LocalDateTime when, String notes) throws Exception {
        Appointment a = new Appointment(customerId, vehicleId, when, "Scheduled", notes);
        appointmentDAO.insert(a);
        return a;
    }

    public boolean updateStatus(int appointmentId, String status) throws Exception {
        return appointmentDAO.updateStatus(appointmentId, status);
    }

    public boolean cancelAppointment(int appointmentId) throws Exception {
        return appointmentDAO.delete(appointmentId);
    }

    public Appointment findById(int appointmentId) throws Exception {
        return appointmentDAO.findById(appointmentId);
    }

    public List<Appointment> listAll() throws Exception {
        return appointmentDAO.findAll();
    }
}

