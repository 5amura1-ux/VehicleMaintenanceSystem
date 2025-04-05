package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.UserService;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserService userService = new UserService();
    private static String loggedInUserId;
    private static String loggedInUserRole;

    public static String getLoggedInUser() {
        return loggedInUserId;
    }

    public static String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Validate input fields
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both username and password");
                return;
            }

            // Authenticate user by comparing raw password
            List<User> users = userService.getAllUsers();
            User authenticatedUser = null;
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    if (user.getStatus().equals("Active")) {
                        authenticatedUser = user;
                        break;
                    } else {
                        errorLabel.setText("User account is inactive");
                        return;
                    }
                }
            }

            if (authenticatedUser != null) {
                loggedInUserId = authenticatedUser.getUserId();
                loggedInUserRole = authenticatedUser.getRoleId();

                // Set the user ID in the database context
                setUserIdInContext(authenticatedUser.getUserId());

                // Load the unified Dashboard.fxml for all roles
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Dashboard.fxml")));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root, WindowConfig.DEFAULT_WINDOW_WIDTH, WindowConfig.DEFAULT_WINDOW_HEIGHT);
                stage.setScene(scene);
                stage.setTitle("Vehicle Maintenance System - Dashboard");

                // Apply window size constraints
                stage.setMinWidth(WindowConfig.MIN_WINDOW_WIDTH);
                stage.setMinHeight(WindowConfig.MIN_WINDOW_HEIGHT);
                stage.setMaxWidth(WindowConfig.MAX_WINDOW_WIDTH);
                stage.setMaxHeight(WindowConfig.MAX_WINDOW_HEIGHT);
            } else {
                errorLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error during login: " + e.getMessage());
        }
    }

    private void setUserIdInContext(String userId) {
        try (Connection conn = HibernateUtil.getSessionFactory().getSessionFactoryOptions()
                .getServiceRegistry().getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class)
                .getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL VEHICLE_MAINTENANCE_PKG.SET_USER_ID(?)}");
            stmt.setString(1, userId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error setting user context: " + e.getMessage());
            alert.showAndWait();
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