package com.germantown.autocare.model;

import java.time.LocalDateTime;

/**
 * Data model for a service appointment.
 */
public class Appointment {

    private int appointmentId;
    private int customerId;
    private int vehicleId;
    private Integer employeeId;
    private LocalDateTime appointmentDate;
    private String status;
    private String notes;

    public Appointment() {}

    public Appointment(int customerId, int vehicleId, LocalDateTime appointmentDate, String status, String notes) {
        this(customerId, vehicleId, appointmentDate, status, notes, null);
    }

    public Appointment(int customerId, int vehicleId, LocalDateTime appointmentDate, String status, String notes,
                       Integer employeeId) {
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.notes = notes;
        this.employeeId = employeeId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

