package com.oscars.vehiclemaintenancesystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SystemSettingsController {
    @FXML private TextField systemNameField;
    @FXML private TextField systemVersionField;
    @FXML private CheckBox enableNotificationsCheckBox;

    @FXML
    public void initialize() {
        // Check role-based access (only Admins can access this view)
        if (!"Admin".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
            try {
                String fxmlFile;
                switch (LoginController.getLoggedInUserRole()) {
                    case "ROLE00003":
                        fxmlFile = "MechanicDashboard.fxml";
                        break;
                    case "ROLE00005":
                        fxmlFile = "SalesRepDashboard.fxml";
                        break;
                    default:
                        fxmlFile = "LoginView.fxml";
                }
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Placeholder for loading system settings (e.g., from a configuration file or database)
        systemNameField.setText("Vehicle Maintenance System");
        systemVersionField.setText("1.0.0");
        enableNotificationsCheckBox.setSelected(true);
    }

    @FXML
    public void saveSettings() {
        // Placeholder for saving system settings (e.g., to a configuration file or database)
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Settings saved successfully!\n" +
                "System Name: " + systemNameField.getText() + "\n" +
                "System Version: " + systemVersionField.getText() + "\n" +
                "Enable Notifications: " + enableNotificationsCheckBox.isSelected());
        alert.showAndWait();
    }

    @FXML
    public void showDashboard() throws IOException {
        loadView("AdminDashboard.fxml");
    }

    @FXML
    public void showCustomerView() throws IOException {
        loadView("CustomerView.fxml");
    }

    @FXML
    public void showVehicleView() throws IOException {
        loadView("VehicleView.fxml");
    }

    @FXML
    public void showAppointmentView() throws IOException {
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showPaymentView() throws IOException {
        loadView("PaymentView.fxml");
    }

    @FXML
    public void showInventoryView() throws IOException {
        loadView("InventoryView.fxml");
    }

    @FXML
    public void showUserView() throws IOException {
        loadView("UserView.fxml");
    }

    @FXML
    public void showNotificationView() throws IOException {
        loadView("NotificationView.fxml");
    }

    @FXML
    public void showServiceManagementView() throws IOException {
        loadView("ServiceManagementView.fxml");
    }

    @FXML
    public void showServicePackageManagementView() throws IOException {
        loadView("ServicePackageManagementView.fxml");
    }

    @FXML
    public void showMechanicAvailabilityView() throws IOException {
        loadView("MechanicAvailabilityView.fxml");
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
        loadView("InvoiceGenerationView.fxml");
    }

    @FXML
    public void showVehicleChecklistView() throws IOException {
        loadView("VehicleChecklistView.fxml");
    }

    @FXML
    public void showServiceCategoryManagementView() throws IOException {
        loadView("ServiceCategoryManagementView.fxml");
    }

    @FXML
    public void showUserActivityLogView() throws IOException {
        loadView("UserActivityLogView.fxml");
    }

    @FXML
    public void showSystemSettingsView() throws IOException {
        loadView("SystemSettingsView.fxml");
    }

    @FXML
    public void showDashboardAnalyticsView() throws IOException {
        loadView("DashboardAnalyticsView.fxml");
    }

    @FXML
    public void showAuditLogView() throws IOException {
        loadView("AuditLogView.fxml");
    }

    @FXML
    public void showErrorLogView() throws IOException {
        loadView("ErrorLogView.fxml");
    }

    @FXML
    public void showCustomerSearchView() throws IOException {
        loadView("CustomerSearchView.fxml");
    }

    @FXML
    public void showVehicleSearchView() throws IOException {
        loadView("VehicleSearchView.fxml");
    }

    @FXML
    public void showAppointmentHistoryView() throws IOException {
        loadView("AppointmentHistoryView.fxml");
    }

    @FXML
    public void showPaymentHistoryView() throws IOException {
        loadView("PaymentHistoryView.fxml");
    }

    @FXML
    public void showInventoryReportView() throws IOException {
        loadView("InventoryReportView.fxml");
    }

    @FXML
    public void showUserProfileView() throws IOException {
        loadView("UserProfileView.fxml");
    }

    @FXML
    public void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage) systemNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) systemNameField.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}