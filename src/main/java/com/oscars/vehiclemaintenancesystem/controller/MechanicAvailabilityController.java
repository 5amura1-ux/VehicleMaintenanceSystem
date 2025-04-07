package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.util.SidebarUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MechanicAvailabilityController {
    @FXML private TextField mechanicIdField;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleIdColumn;
    @FXML private TableColumn<Appointment, String> serviceIdColumn;
    @FXML private TableColumn<Appointment, String> mechanicIdColumn;
    @FXML private TableColumn<Appointment, Date> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private VBox sidebar;

    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        // Check role-based access (only Admins and Mechanics can access this view)
        if (!"ROLE00004".equals(LoginController.getLoggedInUserRole()) && !"ROLE00003".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Mechanics can access this view");
            alert.showAndWait();
            try {
                String fxmlFile;
                switch (LoginController.getLoggedInUserRole()) {
                    case "ROLE00005":
                        fxmlFile = "SalesRepDashboard.fxml";
                        break;
                    default:
                        fxmlFile = "Login.fxml";
                }
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Set up the table columns
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        mechanicIdColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicId"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    @FXML
    public void checkAvailability() {
        try {
            if (mechanicIdField.getText().isEmpty() || datePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a Mechanic ID and select a date");
                alert.showAndWait();
                return;
            }

            String mechanicId = mechanicIdField.getText();
            LocalDate selectedDate = datePicker.getValue();
            Date date = java.util.Date.from(selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

            List<Appointment> appointments = appointmentService.getAppointmentsByMechanic(mechanicId).stream()
                    .filter(appointment -> {
                        Date appointmentDate = appointment.getAppointmentDate();
                        return appointmentDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(selectedDate);
                    })
                    .collect(Collectors.toList());

            appointmentTable.setItems(FXCollections.observableArrayList(appointments));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error checking availability: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) appointmentTable.getScene().getWindow();
        Scene scene = new Scene(root, WindowConfig.DEFAULT_WINDOW_WIDTH, WindowConfig.DEFAULT_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));

        // Apply window size constraints
        stage.setMinWidth(WindowConfig.MIN_WINDOW_WIDTH);
        stage.setMinHeight(WindowConfig.MIN_WINDOW_HEIGHT);
        stage.setMaxWidth(WindowConfig.MAX_WINDOW_WIDTH);
        stage.setMaxHeight(WindowConfig.MAX_WINDOW_HEIGHT);
    }

    @FXML
    public void logout() {
        try {
            loadView("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}