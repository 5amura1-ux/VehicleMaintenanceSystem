package com.oscars.vehiclemaintenancesystem.util;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarUtil {

    /**
     * Populates the sidebar with buttons based on the user's role.
     *
     * @param sidebar The VBox to populate with buttons.
     * @param role    The role of the logged-in user (e.g., ROLE00004 for Admin).
     * @param stage   The Stage to use for navigation (e.g., for logout).
     */
    public static void populateSidebar(VBox sidebar, String role, Stage stage) {
        sidebar.getChildren().clear(); // Clear any existing buttons

        // Add buttons based on role
        switch (role) {
            case "ROLE00004": // Admin
                addButton(sidebar, "🏠 Dashboard", "Dashboard.fxml", stage);
                addButton(sidebar, "👥 Search Customers", "CustomerSearchView.fxml", stage);
                addButton(sidebar, "🚗 Vehicles", "VehicleView.fxml", stage);
                addButton(sidebar, "📅 Appointment History", "AppointmentHistoryView.fxml", stage);
                addButton(sidebar, "💳 Payments", "PaymentView.fxml", stage);
                addButton(sidebar, "📦 Inventory", "InventoryView.fxml", stage);
                addButton(sidebar, "📊 Inventory Report", "InventoryReportView.fxml", stage);
                addButton(sidebar, "👤 Users", "UserView.fxml", stage);
                addButton(sidebar, "🔔 Notifications", "NotificationView.fxml", stage);
                addButton(sidebar, "⚙️ Services", "ServiceManagementView.fxml", stage);
                addButton(sidebar, "📦 Packages", "ServicePackageManagementView.fxml", stage);
                addButton(sidebar, "🔧 Mechanic Availability", "MechanicAvailabilityView.fxml", stage);
                addButton(sidebar, "📜 Audit Log", "AuditLogView.fxml", stage);
                addButton(sidebar, "❗ Error Log", "ErrorLogView.fxml", stage);
                addButton(sidebar, "⚙️ System Settings", "SystemSettingsView.fxml", stage);
                break;
            case "ROLE00003": // Mechanic
                addButton(sidebar, "🏠 Dashboard", "MechanicDashboard.fxml", stage);
                addButton(sidebar, "📅 Appointments", "AppointmentView.fxml", stage);
                addButton(sidebar, "🔧 Mechanic Availability", "MechanicAvailabilityView.fxml", stage);
                addButton(sidebar, "📝 Feedback", "CustomerFeedbackView.fxml", stage);
                addButton(sidebar, "📋 Vehicle Checklist", "VehicleChecklistView.fxml", stage);
                break;
            case "ROLE00005": // SalesRep
                addButton(sidebar, "🏠 Dashboard", "Dashboard.fxml", stage);
                addButton(sidebar, "👥 Book Appointment", "SalesRepBookingView.fxml", stage);
                addButton(sidebar, "📅 Appointment History", "AppointmentHistoryView.fxml", stage);
                addButton(sidebar, "💳 Payments", "PaymentView.fxml", stage);
                addButton(sidebar, "📝 Feedback", "CustomerFeedbackView.fxml", stage);
                addButton(sidebar, "📄 Invoice Generation", "InvoiceGenerationView.fxml", stage);
                break;
        }

        // Add Logout button for all roles
        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setStyle("-fx-pref-width: 150; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14;");
        logoutButton.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(SidebarUtil.class.getResource("/Login.fxml"));
                Scene scene = new Scene(root, WindowConfig.DEFAULT_WINDOW_WIDTH, WindowConfig.DEFAULT_WINDOW_HEIGHT);
                stage.setScene(scene);
                stage.setTitle("Vehicle Maintenance System - Login");

                // Apply window size constraints
                stage.setMinWidth(WindowConfig.MIN_WINDOW_WIDTH);
                stage.setMinHeight(WindowConfig.MIN_WINDOW_HEIGHT);
                stage.setMaxWidth(WindowConfig.MAX_WINDOW_WIDTH);
                stage.setMaxHeight(WindowConfig.MAX_WINDOW_HEIGHT);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error during logout: " + e.getMessage());
                alert.showAndWait();
            }
        });
        sidebar.getChildren().add(logoutButton);
    }

    /**
     * Adds a button to the sidebar with the specified text and FXML file navigation.
     *
     * @param sidebar The VBox to add the button to.
     * @param text    The text to display on the button.
    Hawkins.
     * @param fxmlFile The FXML file to load when the button is clicked.
     * @param stage   The Stage to use for navigation.
     */
    private static void addButton(VBox sidebar, String text, String fxmlFile, Stage stage) {
        Button button = new Button(text);
        button.setStyle("-fx-pref-width: 150; -fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14;");
        if (text.equals("🏠 Dashboard")) {
            button.setStyle("-fx-pref-width: 150; -fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14;");
        }
        button.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(SidebarUtil.class.getResource("/" + fxmlFile));
                Scene scene = new Scene(root, WindowConfig.DEFAULT_WINDOW_WIDTH, WindowConfig.DEFAULT_WINDOW_HEIGHT);
                stage.setScene(scene);
                stage.setTitle("Vehicle Maintenance System - " + fxmlFile.replace(".fxml", ""));

                // Apply window size constraints
                stage.setMinWidth(WindowConfig.MIN_WINDOW_WIDTH);
                stage.setMinHeight(WindowConfig.MIN_WINDOW_HEIGHT);
                stage.setMaxWidth(WindowConfig.MAX_WINDOW_WIDTH);
                stage.setMaxHeight(WindowConfig.MAX_WINDOW_HEIGHT);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading view: " + e.getMessage());
                alert.showAndWait();
            }
        });
        sidebar.getChildren().add(button);
    }
}