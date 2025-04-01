package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Service;
import com.oscars.vehiclemaintenancesystem.service.ServiceService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    private ServiceService serviceService = new ServiceService();

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
                        fxmlFile = "LoginView.fxml";
                }
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        baseCostColumn.setCellValueFactory(new PropertyValueFactory<>("baseCost"));
        estimatedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedTime"));
        loadServices();
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
        // Implementation for updating a service (requires a PL/SQL procedure like UPDATE_SERVICE)
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

    // Navigation methods (only accessible to Admins due to role check in initialize)
    @FXML
    public void showDashboard() throws IOException {
        loadView("AdminDashboard.fxml");
    }

    @FXML
    public void showCustomerView() throws IOException {
        loadView("CustomerView.fxml");
    }

    @FXML
    public void showVehicleView() throws IOException {
        loadView("VehicleView.fxml");
    }

    @FXML
    public void showAppointmentView() throws IOException {
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showPaymentView() throws IOException {
        loadView("PaymentView.fxml");
    }

    @FXML
    public void showInventoryView() throws IOException {
        loadView("InventoryView.fxml");
    }

    @FXML
    public void showUserView() throws IOException {
        loadView("UserView.fxml");
    }

    @FXML
    public void showNotificationView() throws IOException {
        loadView("NotificationView.fxml");
    }

    @FXML
    public void showServiceManagementView() throws IOException {
        loadView("ServiceManagementView.fxml");
    }

    @FXML
    public void showServicePackageManagementView() throws IOException {
        loadView("ServicePackageManagementView.fxml");
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
    public void showNotificationManagementView() throws IOException {
        loadView("NotificationManagementView.fxml");
    }

    @FXML
    public void showInvoiceGenerationView() throws IOException {
        loadView("InvoiceGenerationView.fxml");
    }

    @FXML
    public void showVehicleChecklistView() throws IOException {
        loadView("VehicleChecklistView.fxml");
    }

    @FXML
    public void showServiceCategoryManagementView() throws IOException {
        loadView("ServiceCategoryManagementView.fxml");
    }

    @FXML
    public void showUserActivityLogView() throws IOException {
        loadView("UserActivityLogView.fxml");
    }

    @FXML
    public void showSystemSettingsView() throws IOException {
        loadView("SystemSettingsView.fxml");
    }

    @FXML
    public void showDashboardAnalyticsView() throws IOException {
        loadView("DashboardAnalyticsView.fxml");
    }

    @FXML
    public void showAuditLogView() throws IOException {
        loadView("AuditLogView.fxml");
    }

    @FXML
    public void showErrorLogView() throws IOException {
        loadView("ErrorLogView.fxml");
    }

    @FXML
    public void showCustomerSearchView() throws IOException {
        loadView("CustomerSearchView.fxml");
    }

    @FXML
    public void showVehicleSearchView() throws IOException {
        loadView("VehicleSearchView.fxml");
    }

    @FXML
    public void showAppointmentHistoryView() throws IOException {
        loadView("AppointmentHistoryView.fxml");
    }

    @FXML
    public void showPaymentHistoryView() throws IOException {
        loadView("PaymentHistoryView.fxml");
    }

    @FXML
    public void showInventoryReportView() throws IOException {
        loadView("InventoryReportView.fxml");
    }

    @FXML
    public void showUserProfileView() throws IOException {
        loadView("UserProfileView.fxml");
    }

    @FXML
    public void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage) serviceTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) serviceTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void showRoleManagementView(ActionEvent actionEvent) {
    }
}