package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Customer;
import com.oscars.vehiclemaintenancesystem.model.Vehicle;
import com.oscars.vehiclemaintenancesystem.model.Service;
import com.oscars.vehiclemaintenancesystem.model.ServicePackage;
import com.oscars.vehiclemaintenancesystem.model.Mechanic;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.service.CustomerService;
import com.oscars.vehiclemaintenancesystem.service.VehicleService;
import com.oscars.vehiclemaintenancesystem.service.ServiceService;
import com.oscars.vehiclemaintenancesystem.service.ServicePackageService;
import com.oscars.vehiclemaintenancesystem.service.MechanicService;
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

public class SalesRepBookingController {
    @FXML private TextField customerSearchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerIdColumn;
    @FXML private TableColumn<Customer, String> customerFirstNameColumn;
    @FXML private TableColumn<Customer, String> customerLastNameColumn;
    @FXML private TextField customerFirstNameField;
    @FXML private TextField customerLastNameField;
    @FXML private TextField customerPhoneField;
    @FXML private TextField customerEmailField;
    @FXML private TextField customerAddressField;

    @FXML private TextField vehicleSearchField;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> vehicleIdColumn;
    @FXML private TableColumn<Vehicle, String> vinColumn;
    @FXML private TableColumn<Vehicle, String> makeColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TextField vehicleCustomerIdField;
    @FXML private TextField vehicleVinField;
    @FXML private TextField vehicleMakeField;
    @FXML private TextField vehicleModelField;
    @FXML private TextField vehicleYearField;
    @FXML private TextField vehicleLicensePlateField;
    @FXML private TextField vehicleColorField;

    @FXML private TextField appointmentCustomerIdField;
    @FXML private TextField appointmentVehicleIdField;
    @FXML private ComboBox<Service> serviceComboBox;
    @FXML private ComboBox<ServicePackage> packageComboBox;
    @FXML private ComboBox<Mechanic> mechanicComboBox;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ComboBox<String> timeslotComboBox;
    @FXML private TextArea notesField;
    @FXML private Label resultLabel;
    @FXML private VBox sidebar;
    @FXML private Label customerResultLabel;

    private CustomerService customerService = new CustomerService();
    private VehicleService vehicleService = new VehicleService();
    private AppointmentService appointmentService = new AppointmentService();
    private ServiceService serviceService = new ServiceService();
    private ServicePackageService packageService = new ServicePackageService();
    private MechanicService mechanicService = new MechanicService();

    private Customer selectedCustomer;
    private Vehicle selectedVehicle;

    @FXML
    public void initialize() {
        // Check role-based access (only SalesReps can access this view)
        if (!"ROLE00005".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Sales Representatives can access this view");
            alert.showAndWait();
            try {
                loadView("Dashboard.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Set up customer table columns
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        customerLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Set up vehicle table columns
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        vinColumn.setCellValueFactory(new PropertyValueFactory<>("vin"));
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        // Ensure columns are visible
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        vehicleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Populate dropdowns
        populateDropdowns();

        // Populate timeslot dropdown (e.g., 9:00 AM to 5:00 PM in 30-minute intervals)
        List<String> timeslots = List.of(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00"
        );
        timeslotComboBox.setItems(FXCollections.observableArrayList(timeslots));

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });

        // Load initial data for debugging
        searchCustomer();
    }

    private void populateDropdowns() {
        try {
            // Populate services
            List<Service> services = serviceService.getAllServices();
            serviceComboBox.setItems(FXCollections.observableArrayList(services));
//            System.out.println("Fetched services: " + services.size()); // Debug log
            if (services.isEmpty()) {
                System.out.println("No services found in the database.");
            }

            // Populate packages
            List<ServicePackage> packages = packageService.getAllServicePackages();
            packageComboBox.setItems(FXCollections.observableArrayList(packages));
//            System.out.println("Fetched packages: " + packages.size()); // Debug log
            if (packages.isEmpty()) {
                System.out.println("No service packages found in the database.");
            }

            // Populate mechanics
            List<Mechanic> mechanics = mechanicService.getAllMechanics();
            mechanicComboBox.setItems(FXCollections.observableArrayList(mechanics));
//            System.out.println("Fetched mechanics: " + mechanics.size()); // Debug log
            if (mechanics.isEmpty()) {
                System.out.println("No mechanics found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error populating dropdowns: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void searchCustomer() {
        try {
            String searchText = customerSearchField.getText().trim().toLowerCase();
            List<Customer> customers;
            if (searchText.isEmpty()) {
                customers = customerService.getAllCustomers();
            } else {
                customers = customerService.getAllCustomers().stream()
                        .filter(customer -> customer.getCustomerId().toLowerCase().contains(searchText) ||
                                customer.getFirstName().toLowerCase().contains(searchText) ||
                                customer.getLastName().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
            }

            System.out.println("Fetched customers: " + customers.size()); // Debug log
            for (Customer customer : customers) {
                System.out.println("Customer: ID=" + customer.getCustomerId() + ", FirstName=" + customer.getFirstName() + ", LastName=" + customer.getLastName());
            }

            customerTable.getItems().clear(); // Clear existing items
            customerTable.setItems(FXCollections.observableArrayList(customers));
            if (customers.isEmpty()) {
                resultLabel.setText("No customers found.");
            } else {
                resultLabel.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error searching customers: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void selectCustomer() {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a customer from the table");
            alert.showAndWait();
            return;
        }

        vehicleCustomerIdField.setText(selectedCustomer.getCustomerId());
        appointmentCustomerIdField.setText(selectedCustomer.getCustomerId());
        searchVehicle(); // Refresh vehicle table based on selected customer
    }

    @FXML
    public void addCustomer() {
        try {
            if (customerFirstNameField.getText().isEmpty() || customerLastNameField.getText().isEmpty() ||
                    customerPhoneField.getText().isEmpty() || customerEmailField.getText().isEmpty() ||
                    customerAddressField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields to add a new customer");
                alert.showAndWait();
                return;
            }

            String customerId = customerService.addCustomer(
                    customerFirstNameField.getText(),
                    customerLastNameField.getText(),
                    customerPhoneField.getText(),
                    customerEmailField.getText(),
                    customerAddressField.getText()
            );


            customerResultLabel.setText("Customer added successfully with ID: " + customerId);

            customerFirstNameField.clear();
            customerLastNameField.clear();
            customerPhoneField.clear();
            customerEmailField.clear();
            customerAddressField.clear();
            searchCustomer(); // Refresh customer table
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding customer: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void searchVehicle() {
        try {
            if (selectedCustomer == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a customer first");
                alert.showAndWait();
                return;
            }

            String searchText = vehicleSearchField.getText().trim().toLowerCase();
            List<Vehicle> customerVehicles = vehicleService.getVehiclesByCustomer(selectedCustomer.getCustomerId());
            if (searchText.isEmpty()) {
                System.out.println("Fetched vehicles for customer " + selectedCustomer.getCustomerId() + ": " + customerVehicles.size()); // Debug log
                vehicleTable.getItems().clear(); // Clear existing items
                vehicleTable.setItems(FXCollections.observableArrayList(customerVehicles));
                return;
            }

            List<Vehicle> filteredVehicles = customerVehicles.stream()
                    .filter(vehicle -> vehicle.getVehicleId().toLowerCase().contains(searchText) ||
                            vehicle.getVin().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

//            System.out.println("Filtered vehicles: " + filteredVehicles.size()); // Debug log
            vehicleTable.getItems().clear(); // Clear existing items
            vehicleTable.setItems(FXCollections.observableArrayList(filteredVehicles));
            if (filteredVehicles.isEmpty()) {
                resultLabel.setText("No vehicles found for this customer.");
            } else {
                resultLabel.setText("");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error searching vehicles: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void selectVehicle() {
        selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a vehicle from the table");
            alert.showAndWait();
            return;
        }

        appointmentVehicleIdField.setText(selectedVehicle.getVehicleId());
    }

    @FXML
    public void addVehicle() {
        try {
            if (vehicleCustomerIdField.getText().isEmpty() || vehicleVinField.getText().isEmpty() ||
                    vehicleMakeField.getText().isEmpty() || vehicleModelField.getText().isEmpty() ||
                    vehicleYearField.getText().isEmpty() || vehicleLicensePlateField.getText().isEmpty() ||
                    vehicleColorField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields to add a new vehicle");
                alert.showAndWait();
                return;
            }

            int year;
            try {
                year = Integer.parseInt(vehicleYearField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Year must be a valid number");
                alert.showAndWait();
                return;
            }

            String vehicleId = vehicleService.addVehicle(
                    vehicleCustomerIdField.getText(),
                    vehicleVinField.getText(),
                    vehicleMakeField.getText(),
                    vehicleModelField.getText(),
                    year,
                    vehicleLicensePlateField.getText(),
                    vehicleColorField.getText()
            );

            resultLabel.setText("Vehicle added successfully with ID: " + vehicleId);
            vehicleVinField.clear();
            vehicleMakeField.clear();
            vehicleModelField.clear();
            vehicleYearField.clear();
            vehicleLicensePlateField.clear();
            vehicleColorField.clear();
            searchVehicle(); // Refresh vehicle table
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding vehicle: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void bookAppointment() {
        try {
            // Validate required appointment fields
            if (appointmentCustomerIdField.getText().isEmpty() || appointmentVehicleIdField.getText().isEmpty() ||
                    serviceComboBox.getSelectionModel().getSelectedItem() == null ||
                    mechanicComboBox.getSelectionModel().getSelectedItem() == null ||
                    appointmentDatePicker.getValue() == null ||
                    timeslotComboBox.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a customer, vehicle, service, mechanic, appointment date, and timeslot");
                alert.showAndWait();
                return;
            }

            Service selectedService = serviceComboBox.getSelectionModel().getSelectedItem();
            ServicePackage selectedPackage = packageComboBox.getSelectionModel().getSelectedItem();
            Mechanic selectedMechanic = mechanicComboBox.getSelectionModel().getSelectedItem();
            String selectedTimeslot = timeslotComboBox.getSelectionModel().getSelectedItem();

            LocalDate appointmentDate = appointmentDatePicker.getValue();
            Date date = Date.from(appointmentDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

            String appointmentId = appointmentService.scheduleAppointment(
                    appointmentVehicleIdField.getText(),
                    selectedService.getServiceId(),
                    selectedPackage != null ? selectedPackage.getPackageId() : null,
                    selectedMechanic.getUserId(),
                    date,
                    selectedTimeslot,
                    notesField.getText()
            );

            resultLabel.setText("Appointment booked successfully with ID: " + appointmentId);
            serviceComboBox.getSelectionModel().clearSelection();
            packageComboBox.getSelectionModel().clearSelection();
            mechanicComboBox.getSelectionModel().clearSelection();
            timeslotComboBox.getSelectionModel().clearSelection();
            appointmentDatePicker.setValue(null);
            notesField.clear();
            appointmentCustomerIdField.clear();
            appointmentVehicleIdField.clear();
            selectedCustomer = null;
            selectedVehicle = null;
            vehicleCustomerIdField.clear();
            vehicleTable.getItems().clear();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error booking appointment: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) customerSearchField.getScene().getWindow();
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