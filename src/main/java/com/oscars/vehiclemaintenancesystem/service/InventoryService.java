package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.InventoryDAO;
import com.oscars.vehiclemaintenancesystem.model.InventoryItem;

import java.util.List;

public class InventoryService {
    private final InventoryDAO inventoryDAO = new InventoryDAO();

    public String addInventoryItem(String itemName, int quantity, int lowStockThreshold, double unitPrice) throws Exception {
        return inventoryDAO.insertInventory(itemName, quantity, lowStockThreshold, unitPrice);
    }

    public void updateInventoryItem(String itemId, int quantity) throws Exception {
        inventoryDAO.updateInventory(itemId, quantity);
    }

    public void deleteInventoryItem(String itemId) throws Exception {
        inventoryDAO.deleteInventory(itemId);
    }

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryDAO.getAllInventoryItems();
    }
}