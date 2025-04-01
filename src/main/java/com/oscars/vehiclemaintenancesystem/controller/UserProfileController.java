package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

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
    @FXML private VBox sidebar;

    private final UserService userService = new UserService();

    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @FXML
    public void initialize() {
        // Check role-based access (only Admins can access this view)
        if (!"ROLE00004".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
            try {
                loadView("Dashboard.fxml");
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

        // Populate the sidebar based on role
        populateSidebar(LoginController.getLoggedInUserRole());
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

    private void populateSidebar(String role) {
        sidebar.getChildren().clear(); // Clear any existing buttons

        // Add buttons based on role
        switch (role) {
            case "ROLE00004": // Admin
                addButton("🏠 Dashboard", "Dashboard.fxml");
                addButton("👥 Search Customers", "CustomerSearchView.fxml");
                addButton("🚗 Vehicles", "VehicleView.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("📅 Appointment History", "AppointmentHistory.fxml");
                addButton("💳 Payments", "PaymentView.fxml");
                addButton("📦 Inventory", "InventoryView.fxml");
                addButton("📊 Inventory Report", "InventoryReportView.fxml");
                addButton("👤 Users", "UserView.fxml");
                addButton("🔔 Notifications", "NotificationView.fxml");
                addButton("⚙️ Services", "ServiceManagementView.fxml");
                addButton("📦 Packages", "ServicePackageManagementView.fxml");
                addButton("🔧 Mechanic Availability", "MechanicAvailabilityView.fxml");
                addButton("📜 Audit Log", "AuditLogView.fxml");
                addButton("❗ Error Log", "ErrorLogView.fxml");
                addButton("⚙️ System Settings", "SystemSettingsView.fxml");
                break;
            case "ROLE00003": // Mechanic
                addButton("🏠 Dashboard", "Dashboard.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("🔧 Mechanic Availability", "MechanicAvailabilityView.fxml");
                addButton("📝 Feedback", "CustomerFeedbackView.fxml");
                addButton("📋 Vehicle Checklist", "VehicleChecklistView.fxml");
                break;
            case "ROLE00005": // SalesRep
                addButton("🏠 Dashboard", "Dashboard.fxml");
                addButton("👥 Search Customers", "CustomerSearchView.fxml");
                addButton("🚗 Vehicles", "VehicleView.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("📅 Appointment History", "AppointmentHistory.fxml");
                addButton("💳 Payments", "PaymentView.fxml");
                addButton("📝 Feedback", "CustomerFeedbackView.fxml");
                addButton("📄 Invoice Generation", "InvoiceGenerationView.fxml");
                break;
        }

        // Add Logout button for all roles
        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setStyle("-fx-pref-width: 150; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14;");
        logoutButton.setOnAction(this::logout);
        sidebar.getChildren().add(logoutButton);
    }

    private void addButton(String text, String fxmlFile) {
        Button button = new Button(text);
        button.setStyle("-fx-pref-width: 150; -fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14;");
        if (text.equals("👤 Profile")) {
            button.setStyle("-fx-pref-width: 150; -fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14;");
        }
        button.setOnAction(event -> {
            try {
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading view: " + e.getMessage());
                alert.showAndWait();
            }
        });
        sidebar.getChildren().add(button);
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) userIdField.getScene().getWindow();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) userIdField.getScene().getWindow();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.setTitle("Vehicle Maintenance System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}