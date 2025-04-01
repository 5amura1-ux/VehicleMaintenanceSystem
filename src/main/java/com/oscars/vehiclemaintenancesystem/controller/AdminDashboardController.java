package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.service.CustomerService;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
import com.oscars.vehiclemaintenancesystem.service.VehicleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label totalAppointmentsLabel;
    @FXML private Label totalRevenueLabel;

    private CustomerService customerService = new CustomerService();
    private VehicleService vehicleService = new VehicleService();
    private AppointmentService appointmentService = new AppointmentService();
    private PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
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

        welcomeLabel.setText("Welcome, " + LoginController.getLoggedInUser() + " (Admin)");
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showCustomerView() throws IOException {
        System.out.println("Customer button clicked...");
        loadView("CustomerView.fxml");
    }

    @FXML
    public void showVehicleView() throws IOException {
        System.out.println("Vehicle button clicked...");
        loadView("VehicleView.fxml");
    }

    @FXML
    public void showAppointmentView() throws IOException {
        System.out.println("Appointment button clicked...");
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showPaymentView() throws IOException {
        System.out.println("Payment button clicked...");
        loadView("PaymentView.fxml");
    }

    @FXML
    public void showInventoryView() throws IOException {
        System.out.println("Inventory button clicked...");
        loadView("InventoryView.fxml");
    }

    @FXML
    public void showUserView() throws IOException {
        System.out.println("User button clicked...");
        loadView("UserView.fxml");
    }

    @FXML
    public void showNotificationView() throws IOException {
        System.out.println("Notification button clicked...");
        loadView("NotificationView.fxml");
    }

    @FXML
    public void showServiceManagementView() throws IOException {
        System.out.println("Service Management button clicked...");
        loadView("ServiceManagementView.fxml");
    }

    @FXML
    public void showServicePackageManagementView() throws IOException {
        System.out.println("Service Package Management button clicked...");
        loadView("ServicePackageManagementView.fxml");
    }

    @FXML
    public void showMechanicAvailabilityView() throws IOException {
        System.out.println("Mechanic Availability button clicked...");
        loadView("MechanicAvailabilityView.fxml");
    }

    @FXML
    public void showAuditLogView() throws IOException {
        System.out.println("Audit Log button clicked...");
        loadView("AuditLogView.fxml");
    }

    @FXML
    public void showErrorLogView() throws IOException {
        System.out.println("Error Log button clicked...");
        loadView("ErrorLogView.fxml");
    }

    @FXML
    public void showSystemSettingsView() throws IOException {
        System.out.println("System Settings button clicked...");
        loadView("SystemSettingsView.fxml");
    }

    @FXML
    public void logout() {
        try {
            System.out.println("Logging out button clicked...");
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        System.out.println("Loading view: " + fxmlFile);
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void showCustomerSearchView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("CustomerSearchView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }
}