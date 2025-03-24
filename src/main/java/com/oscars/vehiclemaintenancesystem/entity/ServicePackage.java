package com.oscars.vehiclemaintenancesystem.entity;

import javax.persistence.*;

@Entity
@Table(name = "Service_Packages")
public class ServicePackage {
    @Id
    @Column(name = "package_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String packageId;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_price", nullable = false)
    private double discountPrice;

    public ServicePackage() {}

    public ServicePackage(String packageName, String description, double discountPrice) {
        this.packageName = packageName;
        this.description = description;
        this.discountPrice = discountPrice;
    }

    // Getters and Setters
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double discountPrice) { this.discountPrice = discountPrice; }
}