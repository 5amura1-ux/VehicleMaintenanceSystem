package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Customer;
import com.oscars.vehiclemaintenancesystem.service.CustomerService;
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

public class CustomerSearchController {
    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerIdColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> phoneNumberColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private VBox sidebar;
    @FXML private VBox updateForm;
    @FXML private TextField updateCustomerIdField;
    @FXML private TextField updateFirstNameField;
    @FXML private TextField updateLastNameField;
    @FXML private TextField updatePhoneNumberField;
    @FXML private TextField updateEmailField;
    @FXML private TextField updateAddressField;

    private final CustomerService customerService = new CustomerService();
    private ObservableList<Customer> allCustomers; // Store the full list for filtering

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

        // Set up the table columns using PropertyValueFactory
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Ensure columns are visible
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load customers
        loadCustomers();

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            allCustomers = FXCollections.observableArrayList(customers);
            customerTable.setItems(allCustomers);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading customers: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void searchCustomers() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            customerTable.setItems(allCustomers);
            return;
        }

        ObservableList<Customer> filteredCustomers = allCustomers.filtered(customer ->
                (customer.getFirstName() != null && customer.getFirstName().toLowerCase().contains(searchText)) ||
                        (customer.getLastName() != null && customer.getLastName().toLowerCase().contains(searchText)) ||
                        (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(searchText))
        );

        customerTable.setItems(filteredCustomers);
    }

    @FXML
    public void showUpdateForm() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a customer to update");
            alert.showAndWait();
            return;
        }

        // Populate the form with the selected customer's details
        updateCustomerIdField.setText(selectedCustomer.getCustomerId());
        updateFirstNameField.setText(selectedCustomer.getFirstName());
        updateLastNameField.setText(selectedCustomer.getLastName());
        updatePhoneNumberField.setText(selectedCustomer.getPhoneNumber());
        updateEmailField.setText(selectedCustomer.getEmail());
        updateAddressField.setText(selectedCustomer.getAddress());

        // Show the update form
        updateForm.setVisible(true);
        updateForm.setManaged(true);
    }

    @FXML
    public void updateCustomer() {
        try {
            String customerId = updateCustomerIdField.getText();
            String firstName = updateFirstNameField.getText().trim();
            String lastName = updateLastNameField.getText().trim();
            String phoneNumber = updatePhoneNumberField.getText().trim();
            String email = updateEmailField.getText().trim();
            String address = updateAddressField.getText().trim();

            // Validate required fields
            if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || address.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Update the customer
            customerService.updateCustomer(customerId, firstName, lastName, phoneNumber, email, address);

            // Refresh the table
            loadCustomers();

            // Hide the update form
            updateForm.setVisible(false);
            updateForm.setManaged(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer updated successfully");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating customer: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelUpdate() {
        // Hide the update form and clear fields
        updateForm.setVisible(false);
        updateForm.setManaged(false);
        updateCustomerIdField.clear();
        updateFirstNameField.clear();
        updateLastNameField.clear();
        updatePhoneNumberField.clear();
        updateEmailField.clear();
        updateAddressField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) customerTable.getScene().getWindow();
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