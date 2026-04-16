package com.germantown.autocare.service;

import java.time.LocalDateTime;
import java.util.List;

import com.germantown.autocare.dao.AppointmentDAO;
import com.germantown.autocare.model.Appointment;

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

    /**
     * Check if appointment has linked invoices
     * @return List of invoice IDs, empty if none
     */
    public List<Integer> getLinkedInvoices(int appointmentId) throws Exception {
        return appointmentDAO.getLinkedInvoiceIds(appointmentId);
    }

    public Appointment findById(int appointmentId) throws Exception {
        return appointmentDAO.findById(appointmentId);
    }

    public List<Appointment> listAll() throws Exception {
        return appointmentDAO.findAll();
    }
}

