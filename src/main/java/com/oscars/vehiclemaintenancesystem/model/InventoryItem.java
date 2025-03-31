package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Inventory")
public class InventoryItem {
    @Id
    @Column(name = "item_id")
    private String itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "low_stock_threshold")
    private int lowStockThreshold;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "last_updated")
    @Temporal(TemporalType.DATE)
    private Date lastUpdated;

    // Getters and setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}