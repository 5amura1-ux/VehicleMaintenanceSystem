package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Role;
import com.oscars.vehiclemaintenancesystem.service.RoleService;
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

public class RoleManagementController {
    @FXML private TextField roleNameField;
    @FXML private TableView<Role> roleTable;
    @FXML private TableColumn<Role, String> roleIdColumn;
    @FXML private TableColumn<Role, String> roleNameColumn;
    @FXML private VBox sidebar;

    private final RoleService roleService = new RoleService();

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
        roleIdColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        // Load roles
        loadRoles();

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    @FXML
    public void addRole() {
        try {
            if (roleNameField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a role name");
                alert.showAndWait();
                return;
            }

            String roleId = roleService.addRole(roleNameField.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Role added with ID: " + roleId);
            alert.showAndWait();
            loadRoles();
            roleNameField.clear();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding role: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateRole() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Update Role functionality not implemented yet");
        alert.showAndWait();
    }

    @FXML
    public void deleteRole() {
        Role selectedRole = roleTable.getSelectionModel().getSelectedItem();
        if (selectedRole != null) {
            try {
                roleService.deleteRole(selectedRole.getRoleId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Role deleted successfully");
                alert.showAndWait();
                loadRoles();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting role: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a role to delete");
            alert.showAndWait();
        }
    }

    private void loadRoles() {
        try {
            roleTable.setItems(FXCollections.observableArrayList(roleService.getAllRoles()));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading roles: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) roleTable.getScene().getWindow();
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