package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.AuditLog;
import com.oscars.vehiclemaintenancesystem.service.AuditLogService;
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

public class AuditLogController {
    @FXML private TableView<AuditLog> auditLogTable;
    @FXML private TableColumn<AuditLog, String> logIdColumn;
    @FXML private TableColumn<AuditLog, String> tableNameColumn;
    @FXML private TableColumn<AuditLog, String> actionColumn;
    @FXML private TableColumn<AuditLog, String> userIdColumn;
    @FXML private TableColumn<AuditLog, String> userNameColumn; // New column for user name
    @FXML private TableColumn<AuditLog, String> detailsColumn;
    @FXML private TableColumn<AuditLog, java.util.Date> timestampColumn;
    @FXML private VBox sidebar;

    private final AuditLogService auditLogService = new AuditLogService();

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

        // Set up the table columns
        logIdColumn.setCellValueFactory(new PropertyValueFactory<>("logId"));
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // Ensure columns are visible
        auditLogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load audit logs
        loadAuditLogs();

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadAuditLogs() {
        try {
            List<AuditLog> auditLogs = auditLogService.getAllAuditLogs();
            auditLogTable.setItems(FXCollections.observableArrayList(auditLogs));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading audit logs: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) auditLogTable.getScene().getWindow();
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
    private void logout() {
        // Logout functionality is handled by SidebarUtil
    }
}