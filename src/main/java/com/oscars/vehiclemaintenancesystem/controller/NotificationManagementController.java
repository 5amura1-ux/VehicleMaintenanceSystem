package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Notification;
import com.oscars.vehiclemaintenancesystem.service.NotificationService;
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

public class NotificationManagementController {
    @FXML private TableView<Notification> notificationTable;
    @FXML private TableColumn<Notification, String> notificationIdColumn;
    @FXML private TableColumn<Notification, String> userIdColumn;
    @FXML private TableColumn<Notification, String> messageColumn;
    @FXML private TableColumn<Notification, java.util.Date> createdDateColumn;
    @FXML private VBox sidebar;

    private final NotificationService notificationService = new NotificationService();

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

        // Set up the table columns
        notificationIdColumn.setCellValueFactory(new PropertyValueFactory<>("notificationId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        createdDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));

        // Load notifications
        loadNotifications();

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    @FXML
    public void deleteNotification() {
        Notification selectedNotification = notificationTable.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            try {
                notificationService.deleteNotification(selectedNotification.getNotificationId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Notification deleted successfully");
                alert.showAndWait();
                loadNotifications();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting notification: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a notification to delete");
            alert.showAndWait();
        }
    }

    private void loadNotifications() {
        try {
            notificationTable.setItems(FXCollections.observableArrayList(notificationService.getAllNotifications()));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading notifications: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) notificationTable.getScene().getWindow();
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