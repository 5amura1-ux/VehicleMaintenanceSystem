package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.InventoryItem;
import com.oscars.vehiclemaintenancesystem.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class InventoryController {
    @FXML private TextField itemNameField;
    @FXML private TextField quantityField;
    @FXML private TextField lowStockThresholdField;
    @FXML private TextField unitPriceField;
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, String> itemIdColumn;
    @FXML private TableColumn<InventoryItem, String> itemNameColumn;
    @FXML private TableColumn<InventoryItem, Integer> quantityColumn;
    @FXML private TableColumn<InventoryItem, Integer> lowStockThresholdColumn;
    @FXML private TableColumn<InventoryItem, Double> unitPriceColumn;
    @FXML private TableColumn<InventoryItem, java.util.Date> lastUpdatedColumn;

    private InventoryService inventoryService = new InventoryService();

    @FXML
    public void initialize() {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        lowStockThresholdColumn.setCellValueFactory(new PropertyValueFactory<>("lowStockThreshold"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        lastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));
        loadInventoryItems();
    }

    @FXML
    public void addInventoryItem() {
        try {
            if (itemNameField.getText().isEmpty() || quantityField.getText().isEmpty() || lowStockThresholdField.getText().isEmpty() || unitPriceField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            String itemId = inventoryService.addInventoryItem(
                    itemNameField.getText(),
                    Integer.parseInt(quantityField.getText()),
                    Integer.parseInt(lowStockThresholdField.getText()),
                    Double.parseDouble(unitPriceField.getText())
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inventory item added with ID: " + itemId);
            alert.showAndWait();
            loadInventoryItems();
            clearFields();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding inventory item: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateInventoryItem() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                if (quantityField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a quantity to update");
                    alert.showAndWait();
                    return;
                }

                inventoryService.updateInventoryItem(selectedItem.getItemId(), Integer.parseInt(quantityField.getText()));
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inventory item updated successfully");
                alert.showAndWait();
                loadInventoryItems();
                clearFields();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating inventory item: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an item to update");
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteInventoryItem() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                inventoryService.deleteInventoryItem(selectedItem.getItemId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inventory item deleted successfully");
                alert.showAndWait();
                loadInventoryItems();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting inventory item: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an item to delete");
            alert.showAndWait();
        }
    }

    private void loadInventoryItems() {
        try {
            inventoryTable.setItems(FXCollections.observableArrayList(inventoryService.getAllInventoryItems()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        itemNameField.clear();
        quantityField.clear();
        lowStockThresholdField.clear();
        unitPriceField.clear();
    }

    @FXML
    public void showDashboard() throws IOException {
        String fxmlFile;
        switch (LoginController.getLoggedInUserRole()) {
            case "ROLE00004":
                fxmlFile = "AdminDashboard.fxml";
                break;
            case "ROLE00003":
                fxmlFile = "MechanicDashboard.fxml";
                break;
            case "ROLE00005":
                fxmlFile = "SalesRepDashboard.fxml";
                break;
            default:
                throw new IllegalStateException("Unknown role: " + LoginController.getLoggedInUserRole());
        }
        loadView(fxmlFile);
    }

    @FXML
    public void showCustomerView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("CustomerView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showVehicleView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("VehicleView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAppointmentView() throws IOException {
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showPaymentView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("PaymentView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInventoryView() throws IOException {
        loadView("InventoryView.fxml");
    }

    @FXML
    public void showUserView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("UserView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showNotificationView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("NotificationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServiceManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServiceManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServicePackageManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServicePackageManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showMechanicAvailabilityView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00003")) {
            loadView("MechanicAvailabilityView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Mechanics can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showCustomerFeedbackView() throws IOException {
        loadView("CustomerFeedbackView.fxml");
    }

    @FXML
    public void showNotificationManagementView() throws IOException {
        loadView("NotificationManagementView.fxml");
    }

    @FXML
    public void showInvoiceGenerationView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("InvoiceGenerationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) inventoryTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}