package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.ServiceCategory;
import com.oscars.vehiclemaintenancesystem.service.ServiceCategoryService;
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

public class ServiceCategoryManagementController {
    @FXML private TextField categoryNameField;
    @FXML private TextField descriptionField;
    @FXML private TableView<ServiceCategory> categoryTable;
    @FXML private TableColumn<ServiceCategory, String> categoryIdColumn;
    @FXML private TableColumn<ServiceCategory, String> categoryNameColumn;
    @FXML private TableColumn<ServiceCategory, String> descriptionColumn;
    @FXML private VBox sidebar;

    private final ServiceCategoryService serviceCategoryService = new ServiceCategoryService();

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
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Load service categories
        loadServiceCategories();

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    @FXML
    public void addServiceCategory() {
        try {
            if (categoryNameField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a category name");
                alert.showAndWait();
                return;
            }

            String categoryId = serviceCategoryService.addServiceCategory(
                    categoryNameField.getText(),
                    descriptionField.getText()
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service category added with ID: " + categoryId);
            alert.showAndWait();
            loadServiceCategories();
            clearFields();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding service category: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteServiceCategory() {
        ServiceCategory selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            try {
                serviceCategoryService.deleteServiceCategory(selectedCategory.getCategoryId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service category deleted successfully");
                alert.showAndWait();
                loadServiceCategories();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting service category: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a service category to delete");
            alert.showAndWait();
        }
    }

    private void loadServiceCategories() {
        try {
            categoryTable.setItems(FXCollections.observableArrayList(serviceCategoryService.getAllServiceCategories()));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading service categories: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearFields() {
        categoryNameField.clear();
        descriptionField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) categoryTable.getScene().getWindow();
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