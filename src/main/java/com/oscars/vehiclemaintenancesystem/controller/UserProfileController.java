package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.service.UserService;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserProfileController {
    @FXML private TabPane tabPane;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleIdColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> statusColumn;

    // Fields for creating a new user
    @FXML private TextField newUsernameField;
    @FXML private TextField newPasswordField;
    @FXML private TextField newRoleIdField;
    @FXML private TextField newFirstNameField;
    @FXML private TextField newLastNameField;
    @FXML private TextField newEmailField;
    @FXML private ComboBox<String> newStatusComboBox;

    // Fields for updating a selected user
    @FXML private TextField userIdField;
    @FXML private TextField usernameField;
    @FXML private TextField roleIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Label statusLabel;
    @FXML private VBox sidebar;

    private final UserService userService = new UserService();

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
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleIdColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load all users into the table
        loadAllUsers();

        // Set up status combo box for new user
        newStatusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));

        // Add listener to table selection to populate update form
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                userIdField.setText(newSelection.getUserId());
                usernameField.setText(newSelection.getUsername());
                roleIdField.setText(newSelection.getRoleId());
                firstNameField.setText(newSelection.getFirstName());
                lastNameField.setText(newSelection.getLastName());
                emailField.setText(newSelection.getEmail());
                statusLabel.setText("Status: " + newSelection.getStatus());
                passwordField.setText(""); // Password is not loaded for security reasons
                // Switch to the "Update User" tab
                tabPane.getSelectionModel().select(2); // Index 2 is the "Update User" tab
            }
        });

        // Select the "View Users" tab by default
        tabPane.getSelectionModel().select(0);

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading users: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void createUser() {
        try {
            // Validate input fields
            if (newUsernameField.getText().isEmpty() || newPasswordField.getText().isEmpty() || newRoleIdField.getText().isEmpty() ||
                    newFirstNameField.getText().isEmpty() || newLastNameField.getText().isEmpty() || newEmailField.getText().isEmpty() ||
                    newStatusComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Create new user
            String encryptedPassword = encryptPassword(newPasswordField.getText());
            userService.createUser(
                    newUsernameField.getText(),
                    encryptedPassword,
                    newRoleIdField.getText(),
                    newFirstNameField.getText(),
                    newLastNameField.getText(),
                    newEmailField.getText(),
                    newStatusComboBox.getValue()
            );

            // Show success message and refresh the table
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "User created successfully");
            alert.showAndWait();
            loadAllUsers();

            // Clear the create user form
            newUsernameField.clear();
            newPasswordField.clear();
            newRoleIdField.clear();
            newFirstNameField.clear();
            newLastNameField.clear();
            newEmailField.clear();
            newStatusComboBox.getSelectionModel().clearSelection();

            // Switch to the "View Users" tab to show the updated list
            tabPane.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error creating user: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateProfile() {
        try {
            // Validate input fields
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Update the selected user
            String userId = userIdField.getText();
            String newPassword = passwordField.getText().isEmpty() ? null : encryptPassword(passwordField.getText());

            userService.updateUserProfile(
                    userId,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    newPassword
            );

            // Show success message and refresh the table
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "User updated successfully");
            alert.showAndWait();
            loadAllUsers();

            // Clear the update form
            clearUpdateForm();

            // Switch to the "View Users" tab to show the updated list
            tabPane.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating user: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearUpdateForm() {
        userIdField.clear();
        usernameField.clear();
        roleIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        statusLabel.setText("");
        userTable.getSelectionModel().clearSelection();
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
            loadView("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}