package com.oscars.vehiclemaintenancesystem.ui;

import com.oscars.vehiclemaintenancesystem.dao.UserDAO;
import com.oscars.vehiclemaintenancesystem.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO.Impl();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate credentials
        User user = userDAO.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            // Successful login, open Admin Dashboard
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin_dashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 800));
                stage.setTitle("Admin Dashboard");
                stage.show();
            } catch (Exception e) {
                errorLabel.setText("Error opening dashboard: " + e.getMessage());
            }
        } else {
            // Invalid credentials
            errorLabel.setText("Invalid username or password.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleMouseEnter() {
        loginButton.setStyle("-fx-background-color: #005bb5; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8px 20px; -fx-border-radius: 6px;");
    }

    @FXML
    private void handleMouseExit() {
        loginButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8px 20px; -fx-border-radius: 6px;");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Vehicle Maintenance Management System");
        alert.setContentText("Version 1.0\nDeveloped by Oscars Team");
        alert.showAndWait();
    }
}