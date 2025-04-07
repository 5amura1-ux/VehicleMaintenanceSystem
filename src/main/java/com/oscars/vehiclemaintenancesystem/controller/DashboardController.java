package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.service.CustomerService;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
import com.oscars.vehiclemaintenancesystem.service.UserService;
import com.oscars.vehiclemaintenancesystem.service.VehicleService;
import com.oscars.vehiclemaintenancesystem.util.SidebarUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label totalAppointmentsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label todaysAppointmentsLabel;
    @FXML private TableView<Appointment> recentAppointmentsTable;
    @FXML private TableColumn<Appointment, String> recentAppointmentIdColumn;
    @FXML private TableColumn<Appointment, String> recentVehicleIdColumn;
    @FXML private TableColumn<Appointment, java.util.Date> recentAppointmentDateColumn;
    @FXML private TableColumn<Appointment, String> recentStatusColumn;
    @FXML private Label pendingPaymentsLabel;
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

        // Set up the recent appointments table columns
        recentAppointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        recentVehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        recentAppointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        recentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Delay sidebar population and CSS loading until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, role, stage);
            // Load the CSS stylesheet from the correct path
            stage.getScene().getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        });

        // Display statistics based on role
        try {
            if (role.equals("ROLE00004") || role.equals("ROLE00005")) {
                // Admin and SalesRep stats
                long totalCustomers = customerService.getAllCustomers().size();
                long totalVehicles = vehicleService.getAllVehicles().size();
                long totalAppointments = appointmentService.getAllAppointments().size();
                double totalRevenue = paymentService.getAllPayments().stream()
                        .mapToDouble(Payment::getAmount)
                        .sum();
                long pendingPayments = paymentService.getAllPayments().stream()
                        .filter(payment -> "PENDING".equals(payment.getPaymentStatus()))
                        .count();

                totalCustomersLabel.setText("Total Customers: " + totalCustomers);
                totalVehiclesLabel.setText("Total Vehicles: " + totalVehicles);
                totalAppointmentsLabel.setText("Total Appointments: " + totalAppointments);
                totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue));
                pendingPaymentsLabel.setText("Pending Payments: " + pendingPayments);

                // Load recent appointments (last 5)
                List<Appointment> recentAppointments = appointmentService.getAllAppointments().stream()
                        .sorted((a1, a2) -> a2.getAppointmentDate().compareTo(a1.getAppointmentDate()))
                        .limit(5)
                        .collect(Collectors.toList());
                recentAppointmentsTable.setItems(FXCollections.observableArrayList(recentAppointments));
            } else if (role.equals("ROLE00003")) {
                // Mechanic stats
                List<Appointment> mechanicAppointments = appointmentService.getAppointmentsByMechanic(userId);
                long totalAppointments = mechanicAppointments.size();
                long todaysAppointments = mechanicAppointments.stream()
                        .filter(app -> {
                            if (app.getAppointmentDate() == null) return false;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(new Date());
                            String appointmentDate = sdf.format(app.getAppointmentDate());
                            return today.equals(appointmentDate);
                        })
                        .count();

                totalAppointmentsLabel.setText("Total Appointments: " + totalAppointments);
                todaysAppointmentsLabel.setText("Today's Appointments: " + todaysAppointments);

                // Hide other stats for Mechanic
                totalCustomersLabel.setVisible(false);
                totalVehiclesLabel.setVisible(false);
                totalRevenueLabel.setVisible(false);
                pendingPaymentsLabel.setVisible(false);

                // Load recent appointments (last 5 for this mechanic)
                List<Appointment> recentAppointments = mechanicAppointments.stream()
                        .sorted((a1, a2) -> a2.getAppointmentDate().compareTo(a1.getAppointmentDate()))
                        .limit(5)
                        .collect(Collectors.toList());
                recentAppointmentsTable.setItems(FXCollections.observableArrayList(recentAppointments));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}