package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.util.SidebarUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AppointmentHistoryController {
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleIdColumn;
    @FXML private TableColumn<Appointment, String> vehicleMakeColumn;
    @FXML private TableColumn<Appointment, String> vehicleModelColumn;
    @FXML private TableColumn<Appointment, String> serviceIdColumn;
    @FXML private TableColumn<Appointment, String> serviceNameColumn;
    @FXML private TableColumn<Appointment, String> packageIdColumn;
    @FXML private TableColumn<Appointment, String> packageNameColumn;
    @FXML private TableColumn<Appointment, String> mechanicIdColumn;
    @FXML private TableColumn<Appointment, String> mechanicNameColumn;
    @FXML private TableColumn<Appointment, java.util.Date> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private TableColumn<Appointment, String> notesColumn;
    @FXML private ComboBox<String> searchCriteriaComboBox;
    @FXML private TextField searchField;
    @FXML private VBox sidebar;
    @FXML private VBox updateForm;
    @FXML private TextField updateAppointmentIdField;
    @FXML private TextField updateVehicleIdField;
    @FXML private TextField updateServiceIdField;
    @FXML private TextField updatePackageIdField;
    @FXML private TextField updateMechanicIdField;
    @FXML private DatePicker updateAppointmentDatePicker;
    @FXML private ComboBox<String> updateTimeslotComboBox;
    @FXML private ComboBox<String> updateStatusComboBox;
    @FXML private TextArea updateNotesField;

    private final AppointmentService appointmentService = new AppointmentService();
    private ObservableList<Appointment> allAppointments; // Store the full list for filtering

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

        // Set default search criterion
        searchCriteriaComboBox.getSelectionModel().selectFirst();

        // Populate timeslot dropdown
        updateTimeslotComboBox.setItems(FXCollections.observableArrayList(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00"
        ));

        // Populate status dropdown
        updateStatusComboBox.setItems(FXCollections.observableArrayList(
                "SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        ));

        // Load appointment history
        loadAppointments();

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            allAppointments = FXCollections.observableArrayList(appointments);
            appointmentTable.setItems(allAppointments);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading appointment history: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void searchAppointments() {
        String searchText = searchField.getText().trim().toLowerCase();
        String criterion = searchCriteriaComboBox.getSelectionModel().getSelectedItem();

        if (searchText.isEmpty()) {
            appointmentTable.setItems(allAppointments);
            return;
        }

        ObservableList<Appointment> filteredAppointments = allAppointments.filtered(appointment -> {
            switch (criterion) {
                case "Appointment ID":
                    return appointment.getAppointmentId().toLowerCase().contains(searchText);
                case "Vehicle ID":
                    return appointment.getVehicleId().toLowerCase().contains(searchText);
                case "Service Name":
                    return appointment.getServiceName() != null && appointment.getServiceName().toLowerCase().contains(searchText);
                case "Mechanic Name":
                    return appointment.getMechanicName() != null && appointment.getMechanicName().toLowerCase().contains(searchText);
                case "Appointment Date":
                    if (appointment.getAppointmentDate() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = dateFormat.format(appointment.getAppointmentDate());
                        return dateStr.toLowerCase().contains(searchText);
                    }
                    return false;
                case "Status":
                    return appointment.getStatus() != null && appointment.getStatus().toLowerCase().contains(searchText);
                default:
                    return false;
            }
        });

        appointmentTable.setItems(filteredAppointments);
    }

    @FXML
    public void clearSearch() {
        searchField.clear();
        appointmentTable.setItems(allAppointments);
    }

    @FXML
    public void showUpdateForm() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an appointment to update");
            alert.showAndWait();
            return;
        }

        // Populate the form with the selected appointment's details
        updateAppointmentIdField.setText(selectedAppointment.getAppointmentId());
        updateVehicleIdField.setText(selectedAppointment.getVehicleId());
        updateServiceIdField.setText(selectedAppointment.getServiceId());
        updatePackageIdField.setText(selectedAppointment.getPackageId() != null ? selectedAppointment.getPackageId() : "");
        updateMechanicIdField.setText(selectedAppointment.getMechanicId());
        if (selectedAppointment.getAppointmentDate() != null) {
            updateAppointmentDatePicker.setValue(selectedAppointment.getAppointmentDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        }
        updateTimeslotComboBox.getSelectionModel().select(selectedAppointment.getTimeslot());
        updateStatusComboBox.getSelectionModel().select(selectedAppointment.getStatus());
        updateNotesField.setText(selectedAppointment.getNotes() != null ? selectedAppointment.getNotes() : "");

        // Show the update form
        updateForm.setVisible(true);
        updateForm.setManaged(true);
    }

    @FXML
    public void updateAppointment() {
        try {
            String appointmentId = updateAppointmentIdField.getText();
            String packageId = updatePackageIdField.getText().trim().isEmpty() ? null : updatePackageIdField.getText();
            String mechanicId = updateMechanicIdField.getText();
            LocalDate appointmentDate = updateAppointmentDatePicker.getValue();
            String timeslot = updateTimeslotComboBox.getSelectionModel().getSelectedItem();
            String status = updateStatusComboBox.getSelectionModel().getSelectedItem();
            String notes = updateNotesField.getText();

            // Validate required fields
            if (mechanicId.isEmpty() || appointmentDate == null || timeslot == null || status == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Convert LocalDate to Date
            Date date = Date.from(appointmentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Retrieve the existing appointment to get its invoiceGenerated value
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment not found");
                alert.showAndWait();
                return;
            }

            // Update the appointment
            appointmentService.updateAppointment(appointmentId, packageId, mechanicId, date, timeslot, status, notes, appointment.getInvoiceGenerated());

            // Refresh the table
            loadAppointments();

            // Hide the update form
            updateForm.setVisible(false);
            updateForm.setManaged(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment updated successfully");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating appointment: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelUpdate() {
        // Hide the update form and clear fields
        updateForm.setVisible(false);
        updateForm.setManaged(false);
        updateAppointmentIdField.clear();
        updateVehicleIdField.clear();
        updateServiceIdField.clear();
        updatePackageIdField.clear();
        updateMechanicIdField.clear();
        updateAppointmentDatePicker.setValue(null);
        updateTimeslotComboBox.getSelectionModel().clearSelection();
        updateStatusComboBox.getSelectionModel().clearSelection();
        updateNotesField.clear();
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

}