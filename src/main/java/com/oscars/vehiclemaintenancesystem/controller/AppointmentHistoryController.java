package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class AppointmentHistoryController {
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleIdColumn;
    @FXML private TableColumn<Appointment, String> serviceIdColumn;
    @FXML private TableColumn<Appointment, String> packageIdColumn;
    @FXML private TableColumn<Appointment, String> mechanicIdColumn;
    @FXML private TableColumn<Appointment, String> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private TableColumn<Appointment, String> notesColumn;
    @FXML private VBox sidebar;

    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        // Populate the sidebar based on role
        populateSidebar(LoginController.getLoggedInUserRole());

        // Set up the table columns
        appointmentIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAppointmentId()));
        vehicleIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVehicleId()));
        serviceIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getServiceId()));
        packageIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPackageId()));
        mechanicIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMechanicId()));
        appointmentDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAppointmentDate().toString()));
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        notesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNotes()));

        // Load appointment history
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            appointmentTable.setItems(FXCollections.observableArrayList(appointments));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading appointment history: " + e.getMessage());
            alert.showAndWait();
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
                addButton("📅 Appointments History", "AppointmentHistoryView.fxml");
                addButton("💳 Payments", "PaymentView.fxml");
                addButton("📦 Inventory", "InventoryView.fxml");
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
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("🔧 Mechanic Availability", "MechanicAvailabilityView.fxml");
                addButton("📝 Feedback", "CustomerFeedbackView.fxml");
                addButton("📋 Vehicle Checklist", "VehicleChecklistView.fxml");
                break;
            case "ROLE00005": // SalesRep
                addButton("👥 Search Customers", "CustomerSearchView.fxml");
                 addButton("🚗 Vehicles", "VehicleSearchView.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("📅 Appointments History", "AppointmentHistoryView.fxml");
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
        Stage stage = (Stage) appointmentTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) appointmentTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}