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
import javafx.stage.Stage;

import java.io.IOException;

public class SalesRepDashboardController {
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
        // Check role-based access (only SalesReps can access this view)
        if (!"ROLE00005".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Sales Representatives can access this view");
            alert.showAndWait();
            try {
                String fxmlFile;
                switch (LoginController.getLoggedInUserRole()) {
                    case "ROLE00004":
                        fxmlFile = "AdminDashboard.fxml";
                        break;
                    case "ROLE00003":
                        fxmlFile = "MechanicDashboard.fxml";
                        break;
                    default:
                        fxmlFile = "Login.fxml"; // Update to match the actual file name
                }
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        welcomeLabel.setText("Welcome, " + LoginController.getLoggedInUser() + " (Sales Representative)");
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
    public void showDashboard() throws IOException {
        loadView("SalesRepDashboard.fxml");
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
    public void showCustomerFeedbackView() throws IOException {
        loadView("CustomerFeedbackView.fxml");
    }

    @FXML
    public void showInvoiceGenerationView() throws IOException {
        loadView("InvoiceGenerationView.fxml");
    }

    @FXML
    public void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}