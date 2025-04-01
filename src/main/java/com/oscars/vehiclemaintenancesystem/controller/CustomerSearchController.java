package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Customer;
import com.oscars.vehiclemaintenancesystem.service.CustomerService;
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

public class CustomerSearchController {
    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerIdColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> phoneNumberColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private VBox sidebar;

    private final CustomerService customerService = new CustomerService();

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
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Populate the sidebar based on role
        populateSidebar(LoginController.getLoggedInUserRole());

        // Load all customers
        loadAllCustomers();
    }

    @FXML
    public void searchCustomers() {
        try {
            String searchText = searchField.getText().trim().toLowerCase();
            if (searchText.isEmpty()) {
                loadAllCustomers();
                return;
            }

            List<Customer> filteredCustomers = customerService.getAllCustomers().stream()
                    .filter(customer -> customer.getFirstName().toLowerCase().contains(searchText) ||
                            customer.getLastName().toLowerCase().contains(searchText) ||
                            customer.getEmail().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            customerTable.setItems(FXCollections.observableArrayList(filteredCustomers));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error searching customers: " + e.getMessage());
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
        if (text.equals("👥 Search Customers")) {
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

    private void loadAllCustomers() {
        try {
            customerTable.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) customerTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) customerTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}