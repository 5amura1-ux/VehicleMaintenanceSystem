package com.oscars.vehiclemaintenancesystem.entity;

import javax.persistence.*;

@Entity
@Table(name = "Service_Categories")
public class ServiceCategory {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "description")
    private String description;

    public ServiceCategory() {}

    public ServiceCategory(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    // Getters and Setters
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}