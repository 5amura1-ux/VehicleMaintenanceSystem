package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.service.CustomerService;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
import com.oscars.vehiclemaintenancesystem.service.VehicleService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label totalAppointmentsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private VBox sidebar;

    private final CustomerService customerService = new CustomerService();
    private final VehicleService vehicleService = new VehicleService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final PaymentService paymentService = new PaymentService();

    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 700;

    @FXML
    public void initialize() {
        String role = LoginController.getLoggedInUserRole();
        String username = LoginController.getLoggedInUser();

        // Set welcome message based on role
        switch (role) {
            case "ROLE00004":
                welcomeLabel.setText("Welcome, " + username + " (Admin)");
                break;
            case "ROLE00003":
                welcomeLabel.setText("Welcome, " + username + " (Mechanic)");
                break;
            case "ROLE00005":
                welcomeLabel.setText("Welcome, " + username + " (Sales Representative)");
                break;
            default:
                welcomeLabel.setText("Welcome, " + username);
        }

        // Populate the sidebar based on role
        populateSidebar(role);

        // Display statistics based on role
        try {
            if (role.equals("ROLE00004") || role.equals("ROLE00005")) {
                // Admin and SalesRep can see all stats
                long totalCustomers = customerService.getAllCustomers().size();
                long totalVehicles = vehicleService.getAllVehicles().size();
                long totalAppointments = appointmentService.getAllAppointments().size();
                double totalRevenue = paymentService.getAllPayments().stream()
                        .mapToDouble(Payment::getAmount)
                        .sum();

                totalCustomersLabel.setText("Total Customers: " + totalCustomers);
                totalVehiclesLabel.setText("Total Vehicles: " + totalVehicles);
                totalAppointmentsLabel.setText("Total Appointments: " + totalAppointments);
                totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue));
            } else if (role.equals("ROLE00003")) {
                // Mechanic only sees their appointments
                long totalAppointments = appointmentService.getAppointmentsByMechanic(username).size();
                totalAppointmentsLabel.setText("Total Appointments: " + totalAppointments);

                // Hide other stats for Mechanic
                totalCustomersLabel.setVisible(false);
                totalVehiclesLabel.setVisible(false);
                totalRevenueLabel.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSidebar(String role) {
        sidebar.getChildren().clear(); // Clear any existing buttons

        // Add buttons based on role
        switch (role) {
            case "ROLE00004": // Admin
                addButton("👥 Search Customers", "CustomerSearchView.fxml");
                 addButton("🚗 Vehicles", "VehicleSearchView.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("📅 Appointment History", "AppointmentHistory.fxml");
                addButton("💳 Payments", "PaymentView.fxml");
                addButton("📦 Inventory", "InventoryView.fxml");
                addButton("📊 Inventory Report", "InventoryReportView.fxml");
                addButton("👤 Users", "UserProfileView.fxml");
                addButton("🔔 Notifications", "NotificationView.fxml");
                addButton("⚙️ Services", "ServiceManagementView.fxml");
                addButton("📦 Packages", "ServicePackageManagementView.fxml");
                addButton("🔧 Mechanic Availability", "MechanicAvailabilityView.fxml");
                addButton("📜 Audit Log", "AuditLogView.fxml");
                addButton("❗ Error Log", "ErrorLogView.fxml");
                addButton("⚙️ System Settings", "SystemSettingsView.fxml");
                break;
            case "ROLE00003": // Mechanic
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("🔧 Mechanic Availability", "MechanicAvailabilityView.fxml");
                addButton("📝 Feedback", "CustomerFeedbackView.fxml");
                addButton("📋 Vehicle Checklist", "VehicleChecklistView.fxml");
                break;
            case "ROLE00005": // SalesRep
                addButton("👥 Search Customers", "CustomerSearchView.fxml");
                 addButton("🚗 Vehicles", "VehicleSearchView.fxml");
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
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.setTitle("Vehicle Maintenance System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}