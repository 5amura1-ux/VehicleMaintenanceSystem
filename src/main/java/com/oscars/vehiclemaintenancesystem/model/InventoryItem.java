package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Inventory_Items", schema = "OSCARS") // Use the correct schema
public class InventoryItem {
    @Id
    @Column(name = "item_id")
    private String itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    // Default constructor
    public InventoryItem() {}

    // Parameterized constructor (optional)
    public InventoryItem(String itemId, String itemName, Integer quantity, Integer lowStockThreshold, Double unitPrice, Date lastUpdated) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.unitPrice = unitPrice;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}