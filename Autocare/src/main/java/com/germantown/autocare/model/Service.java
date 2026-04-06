package com.germantown.autocare.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data model for a service offered by the shop (e.g. oil change, brake pad replacement).
 */
public class Service {

    private int serviceId;
    private String serviceName;
    private Integer estimatedDurationMinutes;
    private BigDecimal basePrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Service() {}

    public Service(String serviceName, Integer estimatedDurationMinutes, BigDecimal basePrice) {
        this.serviceName              = serviceName;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.basePrice                = basePrice;
    }

    public int getServiceId()                                                 { return serviceId; }
    public void setServiceId(int serviceId)                                   { this.serviceId = serviceId; }
    public String getServiceName()                                            { return serviceName; }
    public void setServiceName(String serviceName)                            { this.serviceName = serviceName; }
    public Integer getEstimatedDurationMinutes()                              { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }
    public BigDecimal getBasePrice()                                          { return basePrice; }
    public void setBasePrice(BigDecimal basePrice)                            { this.basePrice = basePrice; }
    public LocalDateTime getCreatedAt()                                       { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)                         { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt()                                       { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)                         { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return serviceName + " ($" + basePrice + ")";
    }
}
