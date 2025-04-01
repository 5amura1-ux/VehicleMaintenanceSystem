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
import javafx.stage.Stage;

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

    private FeedbackService feedbackService = new FeedbackService();

    @FXML
    public void initialize() {
        feedbackIdColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackId"));
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        feedbackTextColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackText"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
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

    @FXML
    public void showDashboard() throws IOException {
        String fxmlFile;
        switch (LoginController.getLoggedInUserRole()) {
            case "ROLE00004":
                fxmlFile = "AdminDashboard.fxml";
                break;
            case "ROLE00003":
                fxmlFile = "MechanicDashboard.fxml";
                break;
            case "ROLE00005":
                fxmlFile = "SalesRepDashboard.fxml";
                break;
            default:
                throw new IllegalStateException("Unknown role: " + LoginController.getLoggedInUserRole());
        }
        loadView(fxmlFile);
    }

    @FXML
    public void showAppointmentView() throws IOException {
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showMechanicAvailabilityView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00003")) {
            loadView("MechanicAvailabilityView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Mechanics can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showCustomerFeedbackView() throws IOException {
        loadView("CustomerFeedbackView.fxml");
    }

    @FXML
    public void showVehicleChecklistView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00003")) {
            loadView("VehicleChecklistView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Mechanics can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showCustomerView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("CustomerView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showVehicleView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("VehicleView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showPaymentView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("PaymentView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInvoiceGenerationView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004") || LoginController.getLoggedInUserRole().equals("ROLE00005")) {
            loadView("InvoiceGenerationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showNotificationManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("NotificationManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInventoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("InventoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("UserView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showNotificationView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("NotificationView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServiceManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServiceManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServicePackageManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServicePackageManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showServiceCategoryManagementView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ServiceCategoryManagementView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserActivityLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("UserActivityLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showSystemSettingsView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("SystemSettingsView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showDashboardAnalyticsView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("DashboardAnalyticsView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAuditLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("AuditLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showErrorLogView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("ErrorLogView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showCustomerSearchView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("CustomerSearchView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showVehicleSearchView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("VehicleSearchView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showAppointmentHistoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("AppointmentHistoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showPaymentHistoryView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("PaymentHistoryView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showInventoryReportView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("InventoryReportView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void showUserProfileView() throws IOException {
        if (LoginController.getLoggedInUserRole().equals("ROLE00004")) {
            loadView("UserProfileView.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
        }
    }

    @FXML
    public void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage) feedbackTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) feedbackTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}