package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.ServicePackage;
import com.oscars.vehiclemaintenancesystem.service.ServicePackageService;
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

public class ServicePackageManagementController {
    @FXML private TextField packageNameField;
    @FXML private TextField descriptionField;
    @FXML private TextField discountPriceField;
    @FXML private TableView<ServicePackage> packageTable;
    @FXML private TableColumn<ServicePackage, String> packageIdColumn;
    @FXML private TableColumn<ServicePackage, String> packageNameColumn;
    @FXML private TableColumn<ServicePackage, String> descriptionColumn;
    @FXML private TableColumn<ServicePackage, Double> discountPriceColumn;
    @FXML private VBox sidebar;

    private final ServicePackageService servicePackageService = new ServicePackageService();

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
        packageIdColumn.setCellValueFactory(new PropertyValueFactory<>("packageId"));
        packageNameColumn.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        discountPriceColumn.setCellValueFactory(new PropertyValueFactory<>("discountPrice"));

        // Load service packages
        loadServicePackages();

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading service packages: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearFields() {
        packageNameField.clear();
        descriptionField.clear();
        discountPriceField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) packageTable.getScene().getWindow();
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