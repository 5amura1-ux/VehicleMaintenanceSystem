package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
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
import javafx.stage.*;

import java.io.IOException;
import java.util.List;

public class PaymentController {
    @FXML private TextField appointmentIdField;
    @FXML private TextField paymentMethodField;
    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, String> paymentIdColumn;
    @FXML private TableColumn<Payment, String> appointmentIdColumn;
    @FXML private TableColumn<Payment, java.util.Date> appointmentDateColumn;
    @FXML private TableColumn<Payment, String> vehicleIdColumn;
    @FXML private TableColumn<Payment, String> customerIdColumn;
    @FXML private TableColumn<Payment, Double> amountColumn;
    @FXML private TableColumn<Payment, java.util.Date> paymentDateColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;
    @FXML private TableColumn<Payment, String> paymentStatusColumn;
    @FXML private VBox sidebar;

    private final PaymentService paymentService = new PaymentService();
    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        // Set up the table columns
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // Ensure columns are visible
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load payments
        loadPayments();

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    @FXML
    public void processPayment() {
        try {
            String appointmentId = appointmentIdField.getText().trim();
            String paymentMethod = paymentMethodField.getText().trim();

            // Validate input fields
            if (appointmentId.isEmpty() || paymentMethod.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Validate appointment exists
            List<Appointment> appointments = appointmentService.getAllAppointments();
            Appointment appointment = appointments.stream()
                    .filter(a -> a.getAppointmentId().equals(appointmentId))
                    .findFirst()
                    .orElse(null);

            if (appointment == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Appointment ID does not exist");
                alert.showAndWait();
                return;
            }

            // Validate invoice generated
            if (!"Y".equals(appointment.getInvoiceGenerated())) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No invoice has been generated for this appointment");
                alert.showAndWait();
                return;
            }

            // Process the payment
            String paymentId = paymentService.processPayment(appointmentId, paymentMethod);
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
            List<Payment> payments = paymentService.getAllPayments();
            paymentTable.setItems(FXCollections.observableArrayList(payments));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading payments: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearFields() {
        appointmentIdField.clear();
        paymentMethodField.clear();
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) paymentTable.getScene().getWindow();
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
    private void logout() {
        // Logout functionality is handled by SidebarUtil
    }
}