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
import java.util.List;

public class RolesController {
    @FXML private TabPane tabPane;
    @FXML private TableView<Role> roleTable;
    @FXML private TableColumn<Role, String> roleIdColumn;
    @FXML private TableColumn<Role, String> roleNameColumn;
    @FXML private TableColumn<Role, String> descriptionColumn;

    // Fields for creating a new role
    @FXML private TextField newRoleNameField;
    @FXML private TextArea newDescriptionField;

    // Fields for updating a selected role
    @FXML private TextField updateRoleIdField;
    @FXML private TextField updateRoleNameField;
    @FXML private TextArea updateDescriptionField;

    // Field for deleting a role
    @FXML private TextField deleteRoleIdField;

    @FXML private VBox sidebar;

    private final RoleService roleService = new RoleService();

    @FXML
    public void initialize() {
        // Check role-based access (only Admins can access this view)
        if (!"ROLE00004".equals(LoginController.getLoggedInUserRole())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
            try {
                loadView("Dashboard.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Set up table columns
        roleIdColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Load all roles into the table
        loadAllRoles();

        // Add listener to table selection to populate update form
        roleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateRoleIdField.setText(newSelection.getRoleId());
                updateRoleNameField.setText(newSelection.getRoleName());
                updateDescriptionField.setText(newSelection.getDescription());
                // Switch to the "Update Role" tab
                tabPane.getSelectionModel().select(2); // Index 2 is the "Update Role" tab
            }
        });

        // Select the "View Roles" tab by default
        tabPane.getSelectionModel().select(0);

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            roleTable.setItems(FXCollections.observableArrayList(roles));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading roles: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void createRole() {
        try {
            // Validate input fields
            if (newRoleNameField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in the role name");
                alert.showAndWait();
                return;
            }

            // Create new role
            System.out.println("Creating role with name: " + newRoleNameField.getText() + ", user: " + LoginController.getLoggedInUser());
            String roleId = roleService.createRole(
                    newRoleNameField.getText(),
                    newDescriptionField.getText()
            );

            // Show success message and refresh the table
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Role created successfully with ID: " + roleId);
            alert.showAndWait();
            loadAllRoles();

            // Clear the create role form
            newRoleNameField.clear();
            newDescriptionField.clear();

            // Switch to the "View Roles" tab to show the updated list
            tabPane.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error creating role: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateRole() {
        try {
            // Validate input fields
            if (updateRoleIdField.getText().isEmpty() || updateRoleNameField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Update the selected role
            String roleId = updateRoleIdField.getText();
            roleService.updateRole(
                    roleId,
                    updateRoleNameField.getText(),
                    updateDescriptionField.getText()
            );

            // Show success message and refresh the table
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Role updated successfully");
            alert.showAndWait();
            loadAllRoles();

            // Clear the update form
            clearUpdateForm();

            // Switch to the "View Roles" tab to show the updated list
            tabPane.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating role: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteRole() {
        try {
            if (deleteRoleIdField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a Role ID to delete");
                alert.showAndWait();
                return;
            }

            String roleId = deleteRoleIdField.getText();

            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete role " + roleId + "?");
            if (confirmAlert.showAndWait().get() != ButtonType.OK) {
                return;
            }

            // Delete the role
            roleService.deleteRole(roleId);

            // Show success message and refresh the table
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Role deleted successfully");
            alert.showAndWait();
            loadAllRoles();

            // Clear the delete form
            deleteRoleIdField.clear();

            // Switch to the "View Roles" tab to show the updated list
            tabPane.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting role: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearUpdateForm() {
        updateRoleIdField.clear();
        updateRoleNameField.clear();
        updateDescriptionField.clear();
        roleTable.getSelectionModel().clearSelection();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) sidebar.getScene().getWindow();
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
            LoginController.clearLoggedInUser();
            loadView("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error during logout: " + e.getMessage());
            alert.showAndWait();
        }
    }
}