package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private UserService userService = new UserService();
    private static String loggedInUser;
    private static String loggedInUserRole;

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
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter both username and password");
                alert.showAndWait();
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

                // Redirect to the appropriate dashboard based on role
                String fxmlFile;
                switch (loggedInUserRole) {
                    case "ROLE00004":
                        System.out.println("Admin logged in: " + loggedInUser);
                        fxmlFile = "/AdminDashboard.fxml";
                        break;
                    case "ROLE00003":
                        fxmlFile = "/MechanicDashboard.fxml";
                        break;
                    case "ROLE00005":
                        fxmlFile = "/SalesRepDashboard.fxml";
                        break;
                    default:
                        throw new IllegalStateException("Unknown role: " + loggedInUserRole);
                }
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password, or user is inactive");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Added to log the full stack trace
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error during login: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void handleClose(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    public void handleAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Vehicle Maintenance & Service Management System");
        alert.setContentText("This application is a vehicle maintenance and service management system. It allows users to manage vehicles, maintenance schedules, and service requests. The system has three user roles: Admin, Mechanic, and Sales Representative. Each role has different permissions and capabilities.");
        alert.showAndWait();
    }
}