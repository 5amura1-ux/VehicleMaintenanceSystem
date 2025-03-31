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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

            String encryptedPassword = encryptPassword(password);
            List<User> users = userService.getAllUsers();
            User authenticatedUser = null;
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(encryptedPassword) && user.getStatus().equals("Active")) {
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
                    case "Admin":
                        fxmlFile = "/AdminDashboard.fxml";
                        break;
                    case "Mechanic":
                        fxmlFile = "/MechanicDashboard.fxml";
                        break;
                    case "SalesRep":
                        fxmlFile = "/SalesRepDashboard.fxml";
                        break;
                    default:
                        throw new IllegalStateException("Unknown role: " + loggedInUserRole);
                }
                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password, or user is inactive");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error during login: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

        public void handleClose( ActionEvent event) {
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
//    public void getLoggedInUser(ActionEvent actionEvent) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Logged In User");
//        alert.setHeaderText("Logged In User");
//        alert.setContentText("The logged in user is: " + loggedInUser);
//        alert.showAndWait();
//    }


}