package com.oscars.vehiclemaintenancesystem.ui;

import com.oscars.vehiclemaintenancesystem.dao.ServiceDAO;
import com.oscars.vehiclemaintenancesystem.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminDashboardController {
    @FXML
    private Button userManagementButton;

    @FXML
    private Button inventoryButton;

    @FXML
    private Button paymentInvoiceButton;

    @FXML
    private Button auditLogsButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button modifyVariablesButton;

    @FXML
    private Label systemStatusLabel;

    @FXML
    private Label databaseStatusLabel;

    @FXML
    private Label activeUsersLabel;

    @FXML
    private Label activeServicesLabel;

    private UserDAO userDAO;
    private ServiceDAO serviceDAO;

    @FXML
    public void initialize() {
        // Initialize DAOs
        userDAO = new UserDAO.Impl();
        serviceDAO = new ServiceDAO.Impl();

        // Set system and database status (static for now)
        systemStatusLabel.setText("Active");
        databaseStatusLabel.setText("Active");

        // Fetch data from DAOs
        long activeUsers = userDAO.getAllUsers().size();
        long activeServices = serviceDAO.getAllServices().size();

        // Update labels
        activeUsersLabel.setText(String.valueOf(activeUsers));
        activeServicesLabel.setText(String.valueOf(activeServices));
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userManagementButton.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 650));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error returning to login: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) userManagementButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Vehicle Maintenance Management System");
        alert.setContentText("Version 1.0\nDeveloped by Oscars Team");
        alert.showAndWait();
    }

    @FXML
    private void navigateToUserManagement() {
        System.out.println("Navigate to User Management");
    }

    @FXML
    private void showInventory() {
        System.out.println("Navigate to Inventory Management");
    }

    @FXML
    private void showPaymentInvoice() {
        System.out.println("Navigate to Payment and Invoice");
    }

    @FXML
    private void showAuditLogs() {
        System.out.println("Navigate to Audit Logs");
    }

    @FXML
    private void showReports() {
        System.out.println("Navigate to Reports");
    }

    @FXML
    private void showModifyVariables() {
        System.out.println("Navigate to Modify Variables");
    }
}