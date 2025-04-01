package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Feedback;
import com.oscars.vehiclemaintenancesystem.service.FeedbackService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class CustomerFeedbackController {
    @FXML private TextField appointmentIdField;
    @FXML private TextField customerIdField;
    @FXML private TextField feedbackTextField;
    @FXML private TextField ratingField;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> feedbackIdColumn;
    @FXML private TableColumn<Feedback, String> appointmentIdColumn;
    @FXML private TableColumn<Feedback, String> customerIdColumn;
    @FXML private TableColumn<Feedback, String> feedbackTextColumn;
    @FXML private TableColumn<Feedback, Integer> ratingColumn;
    @FXML private VBox sidebar;

    private final FeedbackService feedbackService = new FeedbackService();

    @FXML
    public void initialize() {
        // Set up the table columns
        feedbackIdColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        feedbackTextColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackText"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Populate the sidebar based on role
        populateSidebar(LoginController.getLoggedInUserRole());

        // Load feedback
        loadFeedback();
    }

    @FXML
    public void submitFeedback() {
        try {
            if (appointmentIdField.getText().isEmpty() || customerIdField.getText().isEmpty() || feedbackTextField.getText().isEmpty() || ratingField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            int rating = Integer.parseInt(ratingField.getText());
            if (rating < 1 || rating > 5) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Rating must be between 1 and 5");
                alert.showAndWait();
                return;
            }

            feedbackService.addFeedback(
                    appointmentIdField.getText(),
                    customerIdField.getText(),
                    feedbackTextField.getText(),
                    rating
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Feedback submitted successfully");
            alert.showAndWait();
            loadFeedback();
            clearFields();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error submitting feedback: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void populateSidebar(String role) {
        sidebar.getChildren().clear(); // Clear any existing buttons

        // Add buttons based on role
        switch (role) {
            case "ROLE00004": // Admin
                addButton("🏠 Dashboard", "Dashboard.fxml");
                addButton("👥 Search Customers", "CustomerSearchView.fxml");
                 addButton("🚗 Vehicles", "VehicleSearchView.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("📅 Appointment History", "AppointmentHistory.fxml");
                addButton("💳 Payments", "PaymentView.fxml");
                addButton("📦 Inventory", "InventoryView.fxml");
                addButton("👤 Users", "UserView.fxml");
                addButton("🔔 Notifications", "NotificationView.fxml");
                addButton("⚙️ Services", "ServiceManagementView.fxml");
                addButton("📦 Packages", "ServicePackageManagementView.fxml");
                addButton("🔧 Mechanic Availability", "MechanicAvailabilityView.fxml");
                addButton("📜 Audit Log", "AuditLogView.fxml");
                addButton("❗ Error Log", "ErrorLogView.fxml");
                addButton("⚙️ System Settings", "SystemSettingsView.fxml");
                break;
            case "ROLE00003": // Mechanic
                addButton("🏠 Dashboard", "Dashboard.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("🔧 Mechanic Availability", "MechanicAvailabilityView.fxml");
                addButton("📝 Feedback", "CustomerFeedbackView.fxml");
                addButton("📋 Vehicle Checklist", "VehicleChecklistView.fxml");
                break;
            case "ROLE00005": // SalesRep
                addButton("🏠 Dashboard", "Dashboard.fxml");
                addButton("👥 Search Customers", "CustomerSearchView.fxml");
                 addButton("🚗 Vehicles", "VehicleSearchView.fxml");
                addButton("📅 Appointments", "AppointmentView.fxml");
                addButton("📅 Appointment History", "AppointmentHistory.fxml");
                addButton("💳 Payments", "PaymentView.fxml");
                addButton("📝 Feedback", "CustomerFeedbackView.fxml");
                addButton("📄 Invoice Generation", "InvoiceGenerationView.fxml");
                break;
        }

        // Add Logout button for all roles
        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setStyle("-fx-pref-width: 150; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14;");
        logoutButton.setOnAction(this::logout);
        sidebar.getChildren().add(logoutButton);
    }

    private void addButton(String text, String fxmlFile) {
        Button button = new Button(text);
        button.setStyle("-fx-pref-width: 150; -fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14;");
        if (text.equals("📝 Feedback")) {
            button.setStyle("-fx-pref-width: 150; -fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14;");
        }
        button.setOnAction(event -> {
            try {
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading view: " + e.getMessage());
                alert.showAndWait();
            }
        });
        sidebar.getChildren().add(button);
    }

    private void loadFeedback() {
        try {
            feedbackTable.setItems(FXCollections.observableArrayList(feedbackService.getAllFeedback()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        appointmentIdField.clear();
        customerIdField.clear();
        feedbackTextField.clear();
        ratingField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) feedbackTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) feedbackTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}