package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;

@Entity
@Table(name = "Services")
public class Service {
    @Id
    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "description")
    private String description;

    @Column(name = "base_cost")
    private double baseCost;

    @Column(name = "estimated_time")
    private int estimatedTime;

    // Getters and setters
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }
    public int getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }
}