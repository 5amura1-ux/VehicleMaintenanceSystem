package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MechanicAvailabilityController {
    @FXML private TextField mechanicIdField;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleIdColumn;
    @FXML private TableColumn<Appointment, String> serviceIdColumn;
    @FXML private TableColumn<Appointment, String> mechanicIdColumn;
    @FXML private TableColumn<Appointment, Date> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private VBox sidebar;

    private final AppointmentService appointmentService = new AppointmentService();

    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @FXML
    public void initialize() {
        // Check role-based access (only Admins and Mechanics can access this view)
        if (!"ROLE00004".equals(LoginController.getLoggedInUserRole()) && !"ROLE00003".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Mechanics can access this view");
            alert.showAndWait();
            try {
                loadView("Dashboard.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Set up the table columns
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        mechanicIdColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicId"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Populate the sidebar based on role
        populateSidebar(LoginController.getLoggedInUserRole());
    }

    @FXML
    public void checkAvailability() {
        try {
            if (mechanicIdField.getText().isEmpty() || datePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a Mechanic ID and select a date");
                alert.showAndWait();
                return;
            }

            String mechanicId = mechanicIdField.getText();
            java.time.LocalDate selectedDate = datePicker.getValue();
            java.util.Date date = java.util.Date.from(selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

            List<Appointment> appointments = appointmentService.getAppointmentsByMechanic(mechanicId).stream()
                    .filter(appointment -> {
                        java.util.Date appointmentDate = appointment.getAppointmentDate();
                        return appointmentDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(selectedDate);
                    })
                    .collect(Collectors.toList());

            appointmentTable.setItems(FXCollections.observableArrayList(appointments));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error checking availability: " + e.getMessage());
            alert.showAndWait();
        }
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
        if (text.equals("🔧 Mechanic Availability")) {
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
        Stage stage = (Stage) appointmentTable.getScene().getWindow();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) appointmentTable.getScene().getWindow();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.setTitle("Vehicle Maintenance System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}