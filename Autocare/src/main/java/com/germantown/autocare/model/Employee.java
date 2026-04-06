package com.germantown.autocare.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data model for an employee (mechanic, service advisor, etc.).
 */
public class Employee {

    private int employeeId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String email;
    private String phone;
    private LocalDate hireDate;
    private BigDecimal hourlyRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Employee() {}

    public Employee(String firstName, String lastName, String jobTitle,
                    String email, String phone, LocalDate hireDate,
                    BigDecimal hourlyRate) {
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.jobTitle   = jobTitle;
        this.email      = email;
        this.phone      = phone;
        this.hireDate   = hireDate;
        this.hourlyRate = hourlyRate;
    }

    public int getEmployeeId()                          { return employeeId; }
    public void setEmployeeId(int employeeId)           { this.employeeId = employeeId; }
    public String getFirstName()                        { return firstName; }
    public void setFirstName(String firstName)          { this.firstName = firstName; }
    public String getLastName()                         { return lastName; }
    public void setLastName(String lastName)            { this.lastName = lastName; }
    public String getJobTitle()                         { return jobTitle; }
    public void setJobTitle(String jobTitle)            { this.jobTitle = jobTitle; }
    public String getEmail()                            { return email; }
    public void setEmail(String email)                  { this.email = email; }
    public String getPhone()                            { return phone; }
    public void setPhone(String phone)                  { this.phone = phone; }
    public LocalDate getHireDate()                      { return hireDate; }
    public void setHireDate(LocalDate hireDate)         { this.hireDate = hireDate; }
    public BigDecimal getHourlyRate()                   { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate)    { this.hourlyRate = hourlyRate; }
    public LocalDateTime getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt()                 { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)   { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + jobTitle + ")";
    }
}
