package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Service;
import com.oscars.vehiclemaintenancesystem.service.ServiceService;
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

public class ServiceManagementController {
    @FXML private TextField categoryIdField;
    @FXML private TextField serviceNameField;
    @FXML private TextField descriptionField;
    @FXML private TextField baseCostField;
    @FXML private TextField estimatedTimeField;
    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, String> serviceIdColumn;
    @FXML private TableColumn<Service, String> categoryIdColumn;
    @FXML private TableColumn<Service, String> serviceNameColumn;
    @FXML private TableColumn<Service, String> descriptionColumn;
    @FXML private TableColumn<Service, Double> baseCostColumn;
    @FXML private TableColumn<Service, Integer> estimatedTimeColumn;
    @FXML private VBox sidebar;

    private final ServiceService serviceService = new ServiceService();

    @FXML
    public void initialize() {
        // Check role-based access (only Admins can access this view)
        if (!"ROLE00004".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
            try {
                String fxmlFile;
                switch (LoginController.getLoggedInUserRole()) {
                    case "ROLE00003":
                        fxmlFile = "MechanicDashboard.fxml";
                        break;
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

        // Set up table columns
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        baseCostColumn.setCellValueFactory(new PropertyValueFactory<>("baseCost"));
        estimatedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));

        // Load services
        loadServices();

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    @FXML
    public void addService() {
        try {
            if (categoryIdField.getText().isEmpty() || serviceNameField.getText().isEmpty() || baseCostField.getText().isEmpty() || estimatedTimeField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            String serviceId = serviceService.addService(
                    categoryIdField.getText(),
                    serviceNameField.getText(),
                    descriptionField.getText(),
                    Double.parseDouble(baseCostField.getText()),
                    Integer.parseInt(estimatedTimeField.getText())
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service added with ID: " + serviceId);
            alert.showAndWait();
            loadServices();
            clearFields();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number format for Base Cost or Estimated Time");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding service: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateService() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Update Service functionality not implemented yet");
        alert.showAndWait();
    }

    @FXML
    public void deleteService() {
        try {
            Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
            if (selectedService == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a service to delete");
                alert.showAndWait();
                return;
            }

            serviceService.deleteService(selectedService.getServiceId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service deleted");
            alert.showAndWait();
            loadServices();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting service: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadServices() {
        try {
            serviceTable.setItems(FXCollections.observableArrayList(serviceService.getAllServices()));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading services: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void clearFields() {
        categoryIdField.clear();
        serviceNameField.clear();
        descriptionField.clear();
        baseCostField.clear();
        estimatedTimeField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) serviceTable.getScene().getWindow();
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