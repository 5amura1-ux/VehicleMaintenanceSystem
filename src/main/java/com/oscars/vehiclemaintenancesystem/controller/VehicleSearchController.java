package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Vehicle;
import com.oscars.vehiclemaintenancesystem.service.VehicleService;
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

import java.io.IOException;
import java.util.List;

public class VehicleSearchController {
    @FXML private TextField searchField;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> vehicleIdColumn;
    @FXML private TableColumn<Vehicle, String> customerIdColumn;
    @FXML private TableColumn<Vehicle, String> vinColumn;
    @FXML private TableColumn<Vehicle, String> makeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, Integer> yearColumn;
    @FXML private TableColumn<Vehicle, String> licensePlateColumn;
    @FXML private TableColumn<Vehicle, String> colorColumn;
    @FXML private VBox sidebar;
    @FXML private VBox updateForm;
    @FXML private TextField updateVehicleIdField;
    @FXML private TextField updateCustomerIdField;
    @FXML private TextField updateVinField;
    @FXML private TextField updateMakeField;
    @FXML private TextField updateModelField;
    @FXML private TextField updateYearField;
    @FXML private TextField updateLicensePlateField;
    @FXML private TextField updateColorField;

    private final VehicleService vehicleService = new VehicleService();
    private ObservableList<Vehicle> allVehicles; // Store the full list for filtering

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
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        vinColumn.setCellValueFactory(new PropertyValueFactory<>("vin"));
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        licensePlateColumn.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));

        // Ensure columns are visible
        vehicleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load all vehicles
        loadAllVehicles();

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadAllVehicles() {
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            allVehicles = FXCollections.observableArrayList(vehicles);
            vehicleTable.setItems(allVehicles);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading vehicles: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void searchVehicles() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            vehicleTable.setItems(allVehicles);
            return;
        }

        ObservableList<Vehicle> filteredVehicles = allVehicles.filtered(vehicle ->
                (vehicle.getVin() != null && vehicle.getVin().toLowerCase().contains(searchText)) ||
                        (vehicle.getMake() != null && vehicle.getMake().toLowerCase().contains(searchText)) ||
                        (vehicle.getModel() != null && vehicle.getModel().toLowerCase().contains(searchText)) ||
                        (vehicle.getLicensePlate() != null && vehicle.getLicensePlate().toLowerCase().contains(searchText))
        );

        vehicleTable.setItems(filteredVehicles);
    }

    @FXML
    public void showUpdateForm() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a vehicle to update");
            alert.showAndWait();
            return;
        }

        // Populate the form with the selected vehicle's details
        updateVehicleIdField.setText(selectedVehicle.getVehicleId());
        updateCustomerIdField.setText(selectedVehicle.getCustomerId());
        updateVinField.setText(selectedVehicle.getVin());
        updateMakeField.setText(selectedVehicle.getMake());
        updateModelField.setText(selectedVehicle.getModel());
        updateYearField.setText(String.valueOf(selectedVehicle.getYear()));
        updateLicensePlateField.setText(selectedVehicle.getLicensePlate());
        updateColorField.setText(selectedVehicle.getColor());

        // Show the update form
        updateForm.setVisible(true);
        updateForm.setManaged(true);
    }

    @FXML
    public void updateVehicle() {
        try {
            String vehicleId = updateVehicleIdField.getText();
            String make = updateMakeField.getText().trim();
            String model = updateModelField.getText().trim();
            String yearText = updateYearField.getText().trim();
            String licensePlate = updateLicensePlateField.getText().trim();
            String color = updateColorField.getText().trim();

            // Validate required fields
            if (make.isEmpty() || model.isEmpty() || yearText.isEmpty() || licensePlate.isEmpty() || color.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Validate year
            int year;
            try {
                year = Integer.parseInt(yearText);
                if (year < 1900 || year > java.time.Year.now().getValue() + 1) {
                    throw new NumberFormatException("Year out of valid range");
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Year must be a valid number between 1900 and " + (java.time.Year.now().getValue() + 1));
                alert.showAndWait();
                return;
            }

            // Update the vehicle
            vehicleService.updateVehicle(vehicleId, make, model, year, licensePlate, color);

            // Refresh the table
            loadAllVehicles();

            // Hide the update form
            updateForm.setVisible(false);
            updateForm.setManaged(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vehicle updated successfully");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating vehicle: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelUpdate() {
        // Hide the update form and clear fields
        updateForm.setVisible(false);
        updateForm.setManaged(false);
        updateVehicleIdField.clear();
        updateCustomerIdField.clear();
        updateVinField.clear();
        updateMakeField.clear();
        updateModelField.clear();
        updateYearField.clear();
        updateLicensePlateField.clear();
        updateColorField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) vehicleTable.getScene().getWindow();
        Scene scene = new Scene(root, WindowConfig.DEFAULT_WINDOW_WIDTH, WindowConfig.DEFAULT_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));

        // Apply window size constraints
        stage.setMinWidth(WindowConfig.MIN_WINDOW_WIDTH);
        stage.setMinHeight(WindowConfig.MIN_WINDOW_HEIGHT);
        stage.setMaxWidth(WindowConfig.MAX_WINDOW_WIDTH);
        stage.setMaxHeight(WindowConfig.MAX_WINDOW_HEIGHT);
    }

}