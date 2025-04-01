package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class PaymentController {
    @FXML private TextField appointmentIdField;
    @FXML private TextField paymentMethodField;
    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, String> paymentIdColumn;
    @FXML private TableColumn<Payment, String> appointmentIdColumn;
    @FXML private TableColumn<Payment, Double> amountColumn;
    @FXML private TableColumn<Payment, java.util.Date> paymentDateColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;
    @FXML private TableColumn<Payment, String> paymentStatusColumn;

    private PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        loadPayments();
    }

    @FXML
    public void processPayment() {
        try {
            if (appointmentIdField.getText().isEmpty() || paymentMethodField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            String paymentId = paymentService.processPayment(
                    appointmentIdField.getText(),
                    paymentMethodField.getText()
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment processed with ID: " + paymentId);
            alert.showAndWait();
            loadPayments();
            clearFields();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error processing payment: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void deletePayment() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            try {
                paymentService.deletePayment(selectedPayment.getPaymentId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment deleted successfully");
                alert.showAndWait();
                loadPayments();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting payment: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a payment to delete");
            alert.showAndWait();
        }
    }

    private void loadPayments() {
        try {
            paymentTable.setItems(FXCollections.observableArrayList(paymentService.getAllPayments()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        appointmentIdField.clear();
        paymentMethodField.clear();
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
    public void showAppointmentView() throws IOException {
        loadView("AppointmentView.fxml");
    }

    @FXML
    public void showPaymentView() throws IOException {
        loadView("PaymentView.fxml");
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
    public void showNotificationManagementView() throws IOException {
        loadView("NotificationManagementView.fxml");
    }

    @FXML
    public void showInvoiceGenerationView() throws IOException {
        loadView("InvoiceGenerationView.fxml");
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
            Stage stage = (Stage) paymentTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) paymentTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}