package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserService userService = new UserService();
    private static String loggedInUser;
    private static String loggedInUserRole;

    // Define standard window size
    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 700;

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both username and password");
                return;
            }

            // Removed encryption, compare raw password directly
            List<User> users = userService.getAllUsers();
            User authenticatedUser = null;
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.getStatus().equals("Active")) {
                    authenticatedUser = user;
                    break;
                }
            }

            if (authenticatedUser != null) {
                loggedInUser = authenticatedUser.getUsername();
                loggedInUserRole = authenticatedUser.getRoleId();

                // Load the unified Dashboard.fxml for all roles
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Dashboard.fxml")));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
                stage.setScene(scene);
                stage.setTitle("Vehicle Maintenance System - Dashboard");
            } else {
                errorLabel.setText("Invalid username or password, or user is inactive");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error during login: " + e.getMessage());
        }
    }

    @FXML
    public void handleClose(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Vehicle Maintenance & Service Management System");
        alert.setContentText("This application is a vehicle maintenance and service management system. It allows users to manage vehicles, maintenance schedules, and service requests. The system has three user roles: Admin, Mechanic, and Sales Representative. Each role has different permissions and capabilities.");
        alert.showAndWait();
    }
}