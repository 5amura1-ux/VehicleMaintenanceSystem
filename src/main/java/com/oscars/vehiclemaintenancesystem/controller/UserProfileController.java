package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserProfileController {
    @FXML private TextField userIdField;
    @FXML private TextField usernameField;
    @FXML private TextField roleIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Label statusLabel;

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Check role-based access (only Admins can access this view)
        if (!"ROLE00004".equals(LoginController.getLoggedInUserRole())) {
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

        // Load the logged-in user's profile
        try {
            User user = userService.getUserByUsername(LoginController.getLoggedInUser());
            if (user != null) {
                userIdField.setText(user.getUserId());
                usernameField.setText(user.getUsername());
                roleIdField.setText(user.getRoleId());
                firstNameField.setText(user.getFirstName());
                lastNameField.setText(user.getLastName());
                emailField.setText(user.getEmail());
                statusLabel.setText("Status: " + user.getStatus());
                passwordField.setText(""); // Password is not loaded for security reasons
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateProfile() {
        try {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            String userId = userIdField.getText();
            String newPassword = passwordField.getText().isEmpty() ? null : encryptPassword(passwordField.getText());

            userService.updateUserProfile(
                    userId,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    newPassword
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile updated successfully");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating profile: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
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
            Stage stage = (Stage) userIdField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) userIdField.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}