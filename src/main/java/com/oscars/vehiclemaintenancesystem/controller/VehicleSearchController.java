package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Vehicle;
import com.oscars.vehiclemaintenancesystem.service.VehicleService;
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
import java.util.List;
import java.util.stream.Collectors;

public class VehicleSearchController {
    @FXML private TextField searchField;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> vehicleIdColumn;
    @FXML private TableColumn<Vehicle, String> customerIdColumn;
    @FXML private TableColumn<Vehicle, String> vinColumn;
    @FXML private TableColumn<Vehicle, String> makeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, Integer> yearColumn;
    @FXML private TableColumn<Vehicle, String> licensePlateColumn;
    @FXML private TableColumn<Vehicle, String> colorColumn;
    @FXML private VBox sidebar;

    private final VehicleService vehicleService = new VehicleService();

    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @FXML
    public void initialize() {
        // Check role-based access (only Admins and SalesReps can access this view)
        if (!"ROLE00004".equals(LoginController.getLoggedInUserRole()) && !"ROLE00005".equals(LoginController.getLoggedInUserRole())) {
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
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        vinColumn.setCellValueFactory(new PropertyValueFactory<>("vin"));
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        licensePlateColumn.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));

        // Populate the sidebar based on role
        populateSidebar(LoginController.getLoggedInUserRole());

        // Load all vehicles
        loadAllVehicles();
    }

    @FXML
    public void searchVehicles() {
        try {
            String searchText = searchField.getText().trim().toLowerCase();
            if (searchText.isEmpty()) {
                loadAllVehicles();
                return;
            }

            List<Vehicle> filteredVehicles = vehicleService.getAllVehicles().stream()
                    .filter(vehicle -> vehicle.getVin().toLowerCase().contains(searchText) ||
                            vehicle.getMake().toLowerCase().contains(searchText) ||
                            vehicle.getModel().toLowerCase().contains(searchText) ||
                            vehicle.getLicensePlate().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            vehicleTable.setItems(FXCollections.observableArrayList(filteredVehicles));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error searching vehicles: " + e.getMessage());
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
                 addButton("🚗 Vehicles", "VehicleSearchView.fxml");
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
        if (text.equals("🚗 Vehicles")) {
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

    private void loadAllVehicles() {
        try {
            vehicleTable.setItems(FXCollections.observableArrayList(vehicleService.getAllVehicles()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) vehicleTable.getScene().getWindow();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) vehicleTable.getScene().getWindow();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.setTitle("Vehicle Maintenance System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}