package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Role;
import com.oscars.vehiclemaintenancesystem.service.RoleService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class RoleManagementController {
    @FXML private TextField roleNameField;
    @FXML private TableView<Role> roleTable;
    @FXML private TableColumn<Role, String> roleIdColumn;
    @FXML private TableColumn<Role, String> roleNameColumn;

    private RoleService roleService = new RoleService();

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

        roleIdColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        loadRoles();
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
        // Implementation for updating a role (requires a PL/SQL procedure like UPDATE_ROLE)
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
        }
    }

    @FXML
    public void showDashboard() throws IOException {
        loadView("Dashboard.fxml");
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
    public void showRoleManagementView() throws IOException {
        loadView("RoleManagementView.fxml");
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
        System.exit(0);
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) roleTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}