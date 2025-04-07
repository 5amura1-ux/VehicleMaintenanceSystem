package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Appointment;
import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.service.AppointmentService;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
import com.oscars.vehiclemaintenancesystem.util.SidebarUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class InvoiceGenerationController {
    @FXML private TextField appointmentIdField;
    @FXML private TextField paymentMethodField;
    @FXML private Label invoiceDetailsLabel;
    @FXML private VBox sidebar;

    private final AppointmentService appointmentService = new AppointmentService();
    private final PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
        // Check role-based access (only Admins and SalesReps can access this view)
        String role = LoginController.getLoggedInUserRole();
        if (!"ROLE00004".equals(role) && !"ROLE00005".equals(role)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins and Sales Representatives can access this view");
            alert.showAndWait();
            try {
                loadView("Dashboard.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Populate the sidebar based on role
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, role, stage);
        });
    }

    @FXML
    public void generateInvoice() {
        try {
            if (appointmentIdField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter an appointment ID");
                alert.showAndWait();
                return;
            }

            String appointmentId = appointmentIdField.getText();
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment not found");
                alert.showAndWait();
                return;
            }

            if ("Y".equals(appointment.getInvoiceGenerated())) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Invoice already generated for this appointment");
                alert.showAndWait();
                return;
            }

            // Mark the appointment as having an invoice generated
            appointment.setInvoiceGenerated("Y");
            appointmentService.updateAppointment(
                    appointment.getAppointmentId(),
                    appointment.getPackageId(),
                    appointment.getMechanicId(),
                    appointment.getAppointmentDate(),
                    appointment.getTimeslot(),
                    appointment.getStatus(),
                    appointment.getNotes(),
                    appointment.getInvoiceGenerated()
            );

            invoiceDetailsLabel.setText("Invoice generated successfully for appointment " + appointmentId + ".\nProceed to process payment.");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error generating invoice: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void processPayment() {
        try {
            if (appointmentIdField.getText().isEmpty() || paymentMethodField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            String appointmentId = appointmentIdField.getText();
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment not found");
                alert.showAndWait();
                return;
            }

            if (!"Y".equals(appointment.getInvoiceGenerated())) {
                Alert alert = new Alert(Alert.AlertType.WARNING, " ");
                alert.showAndWait();
                return;
            }

            String paymentId = paymentService.processPayment(appointmentId, paymentMethodField.getText());
            Payment payment = paymentService.getPaymentById(paymentId);

            if (payment != null) {
                invoiceDetailsLabel.setText(
                        "Payment Processed Successfully!\n" +
                                "Payment ID: " + payment.getPaymentId() + "\n" +
                                "Appointment ID: " + payment.getAppointmentId() + "\n" +
                                "Amount: $" + String.format("%.2f", payment.getAmount()) + "\n" +
                                "Payment Method: " + payment.getPaymentMethod() + "\n" +
                                "Payment Date: " + payment.getPaymentDate()
                );
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to process payment");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error processing payment: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
        Stage stage = (Stage) invoiceDetailsLabel.getScene().getWindow();
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