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

import java.util.List;

public class AppointmentHistoryController {
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleMakeColumn; // New column for vehicle make
    @FXML private TableColumn<Appointment, String> vehicleModelColumn; // New column for vehicle model
    @FXML private TableColumn<Appointment, String> serviceIdColumn;
    @FXML private TableColumn<Appointment, String> serviceNameColumn; // New column for service name
    @FXML private TableColumn<Appointment, String> packageIdColumn;
    @FXML private TableColumn<Appointment, String> packageNameColumn; // New column for package name
    @FXML private TableColumn<Appointment, String> mechanicIdColumn;
    @FXML private TableColumn<Appointment, String> mechanicNameColumn; // New column for mechanic name
    @FXML private TableColumn<Appointment, java.util.Date> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private TableColumn<Appointment, String> notesColumn;
    @FXML private VBox sidebar;

    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        // Set up the table columns using PropertyValueFactory
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        vehicleMakeColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleMake"));
        vehicleModelColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleModel"));
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        packageIdColumn.setCellValueFactory(new PropertyValueFactory<>("packageId"));
        packageNameColumn.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        mechanicIdColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicId"));
        mechanicNameColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicName"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Ensure columns are visible
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load appointment history from the view
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            appointmentTable.setItems(FXCollections.observableArrayList(appointments));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading appointment history: " + e.getMessage());
            alert.showAndWait();
        }

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadView(String fxmlFile) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading view: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void logout() {
        // Logout functionality is handled by SidebarUtil
    }
}