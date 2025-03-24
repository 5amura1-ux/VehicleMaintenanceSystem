package com.oscars.vehiclemaintenancesystem.entity;

import javax.persistence.*;

@Entity
@Table(name = "Services")
public class Service {
    @Id
    @Column(name = "service_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String serviceId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "description")
    private String description;

    @Column(name = "base_cost", nullable = false)
    private double baseCost;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    public Service() {}

    public Service(ServiceCategory category, String serviceName, String description, double baseCost, Integer estimatedTime) {
        this.category = category;
        this.serviceName = serviceName;
        this.description = description;
        this.baseCost = baseCost;
        this.estimatedTime = estimatedTime;
    }

    // Getters and Setters
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public ServiceCategory getCategory() { return category; }
    public void setCategory(ServiceCategory category) { this.category = category; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }
    public Integer getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
}