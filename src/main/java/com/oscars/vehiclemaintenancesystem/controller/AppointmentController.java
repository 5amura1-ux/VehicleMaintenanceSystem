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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AppointmentController {
    @FXML private TextField vehicleIdField;
    @FXML private TextField serviceIdField;
    @FXML private TextField packageIdField;
    @FXML private TextField mechanicIdField;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private TextField notesField;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleIdColumn;
    @FXML private TableColumn<Appointment, String> serviceIdColumn;
    @FXML private TableColumn<Appointment, String> packageIdColumn;
    @FXML private TableColumn<Appointment, String> mechanicIdColumn;
    @FXML private TableColumn<Appointment, Date> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private TableColumn<Appointment, String> notesColumn;
    @FXML private VBox sidebar;

    private AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        // Set up the table columns
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        packageIdColumn.setCellValueFactory(new PropertyValueFactory<>("packageId"));
        mechanicIdColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicId"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Populate the sidebar based on role
        populateSidebar(LoginController.getLoggedInUserRole());

        // Load appointments
        loadAppointments();
    }

    @FXML
    public void scheduleAppointment() {
        try {
            if (vehicleIdField.getText().isEmpty() || (serviceIdField.getText().isEmpty() && packageIdField.getText().isEmpty()) ||
                    mechanicIdField.getText().isEmpty() || appointmentDatePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields (either Service ID or Package ID must be provided)");
                alert.showAndWait();
                return;
            }

            LocalDate localDate = appointmentDatePicker.getValue();
            Date appointmentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            String appointmentId = appointmentService.scheduleAppointment(
                    vehicleIdField.getText(),
                    serviceIdField.getText().isEmpty() ? null : serviceIdField.getText(),
                    packageIdField.getText().isEmpty() ? null : packageIdField.getText(),
                    mechanicIdField.getText(),
                    appointmentDate,
                    notesField.getText()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment scheduled with ID: " + appointmentId);
            alert.showAndWait();
            loadAppointments();
            clearFields();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error scheduling appointment: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelAppointment() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            try {
                appointmentService.deleteAppointment(selectedAppointment.getAppointmentId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment cancelled successfully");
                alert.showAndWait();
                loadAppointments();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error cancelling appointment: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an appointment to cancel");
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

    private void loadAppointments() {
        try {
            appointmentTable.setItems(FXCollections.observableArrayList(appointmentService.getAllAppointments()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        vehicleIdField.clear();
        serviceIdField.clear();
        packageIdField.clear();
        mechanicIdField.clear();
        appointmentDatePicker.setValue(null);
        notesField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) appointmentTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
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