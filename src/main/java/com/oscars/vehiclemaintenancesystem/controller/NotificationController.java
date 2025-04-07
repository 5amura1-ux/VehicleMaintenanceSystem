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

public class NotificationController {
    @FXML private TableView<Notification> notificationTable;
    @FXML private TableColumn<Notification, String> notificationIdColumn;
    @FXML private TableColumn<Notification, String> userIdColumn;
    @FXML private TableColumn<Notification, String> messageColumn;
    @FXML private TableColumn<Notification, java.util.Date> createdDateColumn;
    @FXML private VBox sidebar;

    private final NotificationService notificationService = new NotificationService();

    @FXML
    public void initialize() {
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