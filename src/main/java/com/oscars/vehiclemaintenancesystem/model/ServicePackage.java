package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;

@Entity
@Table(name = "Service_Packages")
public class ServicePackage {
    @Id
    @Column(name = "package_id")
    private String packageId;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_price")
    private double discountPrice;

    // Getters and setters
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double discountPrice) { this.discountPrice = discountPrice; }
}