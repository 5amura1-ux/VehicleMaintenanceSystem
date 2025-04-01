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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleChecklistController {
    @FXML private TextField vehicleIdField;
    @FXML private TextArea checklistField;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleIdColumn;
    @FXML private TableColumn<Appointment, String> mechanicIdColumn;
    @FXML private TableColumn<Appointment, java.util.Date> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;

    private AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        mechanicIdColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicId"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @FXML
    public void checkVehicle() {
        try {
            if (vehicleIdField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a Vehicle ID");
                alert.showAndWait();
                return;
            }

            String vehicleId = vehicleIdField.getText();
            List<Appointment> appointments = appointmentService.getAllAppointments().stream()
                    .filter(appointment -> appointment.getVehicleId().equals(vehicleId))
                    .collect(Collectors.toList());

            appointmentTable.setItems(FXCollections.observableArrayList(appointments));

            // Placeholder for checklist generation (e.g., fetch from a database or generate dynamically)
            checklistField.setText("Vehicle Checklist for Vehicle ID: " + vehicleId + "\n" +
                    "1. Check engine oil level: [ ] Pass [ ] Fail\n" +
                    "2. Inspect brakes: [ ] Pass [ ] Fail\n" +
                    "3. Check tire pressure: [ ] Pass [ ] Fail\n" +
                    "4. Test battery: [ ] Pass [ ] Fail\n" +
                    "5. Inspect lights: [ ] Pass [ ] Fail");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error checking vehicle: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void showDashboard() throws IOException {
        String fxmlFile;
        switch (LoginController.getLoggedInUserRole()) {
            case "ROLE00004":
                fxmlFile = "AdminDashboard.fxml";
                break;
            case "ROLE00003":
                fxmlFile = "MechanicDashboard.fxml";
                break;
            default:
                fxmlFile = "LoginView.fxml"; // Fallback for unauthorized access
        }
        loadView(fxmlFile);
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
    public void showCustomerView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("CustomerView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showVehicleView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("VehicleView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showPaymentView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("PaymentView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInvoiceGenerationView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("InvoiceGenerationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showNotificationManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("NotificationManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInventoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("InventoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("UserView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showNotificationView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("NotificationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServiceManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServiceManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServicePackageManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServicePackageManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServiceCategoryManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServiceCategoryManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserActivityLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("UserActivityLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showSystemSettingsView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("SystemSettingsView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showDashboardAnalyticsView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("DashboardAnalyticsView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAuditLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("AuditLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showErrorLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ErrorLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
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

    @FXML
    public void showVehicleSearchView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("VehicleSearchView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAppointmentHistoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("AppointmentHistoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showPaymentHistoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("PaymentHistoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInventoryReportView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("InventoryReportView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserProfileView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("UserProfileView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage) vehicleIdField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) vehicleIdField.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}