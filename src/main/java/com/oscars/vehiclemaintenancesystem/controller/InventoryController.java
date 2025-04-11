package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.InventoryItem;
import com.oscars.vehiclemaintenancesystem.service.InventoryService;
import com.oscars.vehiclemaintenancesystem.util.SidebarUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryController {
    @FXML private TextField addItemNameField;
    @FXML private TextField addQuantityField;
    @FXML private TextField addLowStockThresholdField;
    @FXML private TextField addUnitPriceField;
    @FXML private ComboBox<String> updateItemComboBox;
    @FXML private TextField updateQuantityField;
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, String> itemIdColumn;
    @FXML private TableColumn<InventoryItem, String> itemNameColumn;
    @FXML private TableColumn<InventoryItem, Integer> quantityColumn;
    @FXML private TableColumn<InventoryItem, Integer> lowStockThresholdColumn;
    @FXML private TableColumn<InventoryItem, Double> unitPriceColumn;
    @FXML private TableColumn<InventoryItem, java.util.Date> lastUpdatedColumn;
    @FXML private VBox sidebar;

    private final InventoryService inventoryService = new InventoryService();
    private ObservableList<InventoryItem> inventoryItems;

    @FXML
    public void initialize() {
        // Check role-based access (only Admins and SalesReps can access this view)
        String role = LoginController.getLoggedInUserRole();
        if (!"ROLE00004".equals(role) && !"ROLE00005".equals(role) && !"ROLE00003".equals(role)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
            try {
                loadView("Dashboard.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Set up the table columns
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        lowStockThresholdColumn.setCellValueFactory(new PropertyValueFactory<>("lowStockThreshold"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        lastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));

        // Ensure columns are visible
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load inventory items and populate dropdown
        loadInventoryItems();

        // Set up table selection listener to sync with dropdown
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateItemComboBox.getSelectionModel().select(newSelection.getItemName() + " (" + newSelection.getItemId() + ")");
            }
        });

        // Set up dropdown selection listener to sync with table
        updateItemComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String itemId = newSelection.substring(newSelection.indexOf("(") + 1, newSelection.indexOf(")"));
                InventoryItem selectedItem = inventoryItems.stream()
                        .filter(item -> item.getItemId().equals(itemId))
                        .findFirst()
                        .orElse(null);
                if (selectedItem != null) {
                    inventoryTable.getSelectionModel().select(selectedItem);
                }
            }
        });

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    @FXML
    public void addInventoryItem() {
        try {
            if (addItemNameField.getText().isEmpty() || addQuantityField.getText().isEmpty() ||
                    addLowStockThresholdField.getText().isEmpty() || addUnitPriceField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields to add an item");
                alert.showAndWait();
                return;
            }

            String itemId = inventoryService.addInventoryItem(
                    addItemNameField.getText(),
                    Integer.parseInt(addQuantityField.getText()),
                    Integer.parseInt(addLowStockThresholdField.getText()),
                    Double.parseDouble(addUnitPriceField.getText())
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inventory item added with ID: " + itemId);
            alert.showAndWait();
            loadInventoryItems();
            clearAddFields();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Quantity, Low Stock Threshold, and Unit Price must be valid numbers");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding inventory item: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateInventoryItem() {
        try {
            String selectedItem = updateItemComboBox.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an item to update");
                alert.showAndWait();
                return;
            }

            if (updateQuantityField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a quantity to update");
                alert.showAndWait();
                return;
            }

            String itemId = selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.indexOf(")"));
            int newQuantity = Integer.parseInt(updateQuantityField.getText());

            inventoryService.updateInventoryItem(itemId, newQuantity);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inventory item updated successfully");
            alert.showAndWait();
            loadInventoryItems();
            updateQuantityField.clear();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Quantity must be a valid number");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating inventory item: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadInventoryItems() {
        try {
            inventoryItems = FXCollections.observableArrayList(inventoryService.getAllInventoryItems());
            System.out.println("Loaded " + inventoryItems.size() + " items into inventoryItems list"); // Debug log
            inventoryTable.setItems(inventoryItems);
            System.out.println("Set " + inventoryItems.size() + " items to inventoryTable"); // Debug log

            // Populate the dropdown with item names and IDs
            List<String> itemNames = inventoryItems.stream()
                    .map(item -> item.getItemName() + " (" + item.getItemId() + ")")
                    .collect(Collectors.toList());
            updateItemComboBox.setItems(FXCollections.observableArrayList(itemNames));
            System.out.println("Set " + itemNames.size() + " items to updateItemComboBox"); // Debug log
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading inventory items: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearAddFields() {
        addItemNameField.clear();
        addQuantityField.clear();
        addLowStockThresholdField.clear();
        addUnitPriceField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) inventoryTable.getScene().getWindow();
        Scene scene = new Scene(root, WindowConfig.DEFAULT_WINDOW_WIDTH, WindowConfig.DEFAULT_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));

        // Apply window size constraints
        stage.setMinWidth(WindowConfig.MIN_WINDOW_WIDTH);
        stage.setMinHeight(WindowConfig.MIN_WINDOW_HEIGHT);
        stage.setMaxWidth(WindowConfig.MAX_WINDOW_WIDTH);
        stage.setMaxHeight(WindowConfig.MAX_WINDOW_HEIGHT);
    }

}