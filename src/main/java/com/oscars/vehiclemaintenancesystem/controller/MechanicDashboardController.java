package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MechanicDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalAppointmentsLabel;

    private AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        // Check role-based access (only Mechanics can access this view)
        if (!"ROLE00003".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Mechanics can access this view");
            alert.showAndWait();
            try {
                String fxmlFile;
                switch (LoginController.getLoggedInUserRole()) {
                    case "ROLE00004":
                        fxmlFile = "AdminDashboard.fxml";
                        break;
                    case "ROLE00005":
                        fxmlFile = "SalesRepDashboard.fxml";
                        break;
                    default:
                        fxmlFile = "Login.fxml"; // Corrected to match the actual file name
                }
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        welcomeLabel.setText("Welcome, " + LoginController.getLoggedInUser() + " (Mechanic)");
        try {
            long totalAppointments = appointmentService.getAppointmentsByMechanic(LoginController.getLoggedInUser()).size();
            totalAppointmentsLabel.setText("Total Appointments: " + totalAppointments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showDashboard() throws IOException {
        loadView("MechanicDashboard.fxml");
    }

    @FXML
    public void showAppointmentView() throws IOException {
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showMechanicAvailabilityView() throws IOException {
        loadView("MechanicAvailabilityView.fxml");
    }

    @FXML
    public void showCustomerFeedbackView() throws IOException {
        loadView("CustomerFeedbackView.fxml");
    }

    @FXML
    public void showVehicleChecklistView() throws IOException {
        loadView("VehicleChecklistView.fxml");
    }

    @FXML
    public void logout() {
        try {
            
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml")); // Corrected to match the actual file name
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