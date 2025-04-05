package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.service.CustomerService;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
import com.oscars.vehiclemaintenancesystem.service.UserService;
import com.oscars.vehiclemaintenancesystem.service.VehicleService;
import com.oscars.vehiclemaintenancesystem.util.SidebarUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        String role = LoginController.getLoggedInUserRole();
        String userId = LoginController.getLoggedInUser(); // Now returns user_id

        // Fetch the username using the user_id
        String username = "Unknown";
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                username = user.getUsername();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, role, stage);
        });

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
                long totalAppointments = appointmentService.getAppointmentsByMechanic(userId).size();
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

    @FXML
    private void logout() {
        // Logout functionality is handled by SidebarUtil
    }
}