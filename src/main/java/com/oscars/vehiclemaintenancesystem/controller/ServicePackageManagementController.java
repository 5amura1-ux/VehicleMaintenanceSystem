package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.ServicePackage;
import com.oscars.vehiclemaintenancesystem.service.ServicePackageService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ServicePackageManagementController {
    @FXML private TextField packageNameField;
    @FXML private TextField descriptionField;
    @FXML private TextField discountPriceField;
    @FXML private TableView<ServicePackage> packageTable;
    @FXML private TableColumn<ServicePackage, String> packageIdColumn;
    @FXML private TableColumn<ServicePackage, String> packageNameColumn;
    @FXML private TableColumn<ServicePackage, String> descriptionColumn;
    @FXML private TableColumn<ServicePackage, Double> discountPriceColumn;

    private ServicePackageService servicePackageService = new ServicePackageService();

    @FXML
    public void initialize() {
        packageIdColumn.setCellValueFactory(new PropertyValueFactory<>("packageId"));
        packageNameColumn.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        discountPriceColumn.setCellValueFactory(new PropertyValueFactory<>("discountPrice"));
        loadServicePackages();
    }

    @FXML
    public void addServicePackage() {
        try {
            if (packageNameField.getText().isEmpty() || discountPriceField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            String packageId = servicePackageService.addServicePackage(
                    packageNameField.getText(),
                    descriptionField.getText(),
                    Double.parseDouble(discountPriceField.getText())
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service package added with ID: " + packageId);
            alert.showAndWait();
            loadServicePackages();
            clearFields();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding service package: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteServicePackage() {
        ServicePackage selectedPackage = packageTable.getSelectionModel().getSelectedItem();
        if (selectedPackage != null) {
            try {
                servicePackageService.deleteServicePackage(selectedPackage.getPackageId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service package deleted successfully");
                alert.showAndWait();
                loadServicePackages();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting service package: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a service package to delete");
            alert.showAndWait();
        }
    }

    private void loadServicePackages() {
        try {
            packageTable.setItems(FXCollections.observableArrayList(servicePackageService.getAllServicePackages()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        packageNameField.clear();
        descriptionField.clear();
        discountPriceField.clear();
    }

    @FXML
    public void showDashboard() throws IOException {
        String fxmlFile;
        switch (LoginController.getLoggedInUserRole()) {
            case "Admin":
                fxmlFile = "AdminDashboard.fxml";
                break;
            case "Mechanic":
                fxmlFile = "MechanicDashboard.fxml";
                break;
            case "SalesRep":
                fxmlFile = "SalesRepDashboard.fxml";
                break;
            default:
                throw new IllegalStateException("Unknown role: " + LoginController.getLoggedInUserRole());
        }
        loadView(fxmlFile);
    }

    @FXML
    public void showCustomerView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin") || LoginController.getLoggedInUserRole().equals("SalesRep")) {
            loadView("CustomerView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showVehicleView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin") || LoginController.getLoggedInUserRole().equals("SalesRep")) {
            loadView("VehicleView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAppointmentView() throws IOException {
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showPaymentView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin") || LoginController.getLoggedInUserRole().equals("SalesRep")) {
            loadView("PaymentView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInventoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("InventoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("UserView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showNotificationView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("NotificationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServiceManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("ServiceManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServicePackageManagementView() throws IOException {
        loadView("ServicePackageManagementView.fxml");
    }

    @FXML
    public void showMechanicAvailabilityView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin") || LoginController.getLoggedInUserRole().equals("Mechanic")) {
            loadView("MechanicAvailabilityView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Mechanics can access this view");
            alert.showAndWait();
        }
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
        if (LoginController.getLoggedInUserRole().equals("Admin") || LoginController.getLoggedInUserRole().equals("SalesRep")) {
            loadView("InvoiceGenerationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showVehicleChecklistView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin") || LoginController.getLoggedInUserRole().equals("Mechanic")) {
            loadView("VehicleChecklistView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Mechanics can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServiceCategoryManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("ServiceCategoryManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserActivityLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("UserActivityLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showSystemSettingsView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("SystemSettingsView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showDashboardAnalyticsView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("DashboardAnalyticsView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAuditLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("AuditLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showErrorLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("ErrorLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showCustomerSearchView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("CustomerSearchView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showVehicleSearchView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("VehicleSearchView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAppointmentHistoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("AppointmentHistoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showPaymentHistoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("PaymentHistoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInventoryReportView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
            loadView("InventoryReportView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserProfileView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("Admin")) {
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
            Stage stage = (Stage) packageTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) packageTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}