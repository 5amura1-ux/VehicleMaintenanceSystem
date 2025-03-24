package com.oscars.vehiclemaintenancesystem.entity;

import javax.persistence.*;

@Entity
@Table(name = "Service_Package_Details")
@IdClass(ServicePackageDetailId.class)
public class ServicePackageDetail {
    @Id
    @Column(name = "package_id")
    private String packageId;

    @Id
    @Column(name = "service_id")
    private String serviceId;

    @ManyToOne
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    private ServicePackage servicePackage;

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    public ServicePackageDetail() {}

    public ServicePackageDetail(String packageId, String serviceId, ServicePackage servicePackage, Service service) {
        this.packageId = packageId;
        this.serviceId = serviceId;
        this.servicePackage = servicePackage;
        this.service = service;
    }

    // Getters and Setters
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public ServicePackage getServicePackage() { return servicePackage; }
    public void setServicePackage(ServicePackage servicePackage) { this.servicePackage = servicePackage; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}