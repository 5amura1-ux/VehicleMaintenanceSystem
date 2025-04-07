package com.oscars.vehiclemaintenancesystem.controller;

import com.oscars.vehiclemaintenancesystem.config.WindowConfig;
import com.oscars.vehiclemaintenancesystem.model.Payment;
import com.oscars.vehiclemaintenancesystem.service.PaymentService;
import com.oscars.vehiclemaintenancesystem.util.SidebarUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentHistoryController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchCriteriaComboBox;
    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, String> paymentIdColumn;
    @FXML private TableColumn<Payment, String> appointmentIdColumn;
    @FXML private TableColumn<Payment, Double> amountColumn;
    @FXML private TableColumn<Payment, java.util.Date> paymentDateColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;
    @FXML private TableColumn<Payment, String> paymentStatusColumn;
    @FXML private VBox sidebar;
    @FXML private VBox updateForm;
    @FXML private TextField updatePaymentIdField;
    @FXML private TextField updateAppointmentIdField;
    @FXML private TextField updateAmountField;
    @FXML private TextField updatePaymentDateField;
    @FXML private TextField updatePaymentMethodField;
    @FXML private ComboBox<String> updatePaymentStatusComboBox;

    private final PaymentService paymentService = new PaymentService();
    private ObservableList<Payment> allPayments; // Store the full list for filtering

    @FXML
    public void initialize() {
        // Check role-based access (only Admins can access this view)
        String role = LoginController.getLoggedInUserRole();
        if (!"ROLE00004".equals(role) && !"ROLE00005".equals(role)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Access Denied: Only Admins can access this view");
            alert.showAndWait();
            try {
                String fxmlFile;
                switch (LoginController.getLoggedInUserRole()) {
                    case "ROLE00003":
                        fxmlFile = "MechanicDashboard.fxml";
                        break;
                    case "ROLE00005":
                        fxmlFile = "SalesRepDashboard.fxml";
                        break;
                    default:
                        fxmlFile = "LoginView.fxml";
                }
                loadView(fxmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Set up the table columns
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        // Ensure columns are visible
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set default search criterion
        searchCriteriaComboBox.getSelectionModel().selectFirst();

        // Populate payment status dropdown
        updatePaymentStatusComboBox.setItems(FXCollections.observableArrayList(
                "COMPLETED", "PENDING", "FAILED"
        ));

        // Load payments for completed appointments
        loadPayments();

        // Delay sidebar population until the Scene is fully constructed
        Platform.runLater(() -> {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            SidebarUtil.populateSidebar(sidebar, LoginController.getLoggedInUserRole(), stage);
        });
    }

    private void loadPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            allPayments = FXCollections.observableArrayList(payments);
            paymentTable.setItems(allPayments);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading payments: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void searchPayments() {
        String searchText = searchField.getText().trim().toLowerCase();
        String criterion = searchCriteriaComboBox.getSelectionModel().getSelectedItem();

        if (searchText.isEmpty()) {
            paymentTable.setItems(allPayments);
            return;
        }

        ObservableList<Payment> filteredPayments = allPayments.filtered(payment -> {
            switch (criterion) {
                case "Payment ID":
                    return payment.getPaymentId().toLowerCase().contains(searchText);
                case "Appointment ID":
                    return payment.getAppointmentId().toLowerCase().contains(searchText);
                case "Payment Method":
                    return payment.getPaymentMethod() != null && payment.getPaymentMethod().toLowerCase().contains(searchText);
                case "Payment Status":
                    return payment.getPaymentStatus() != null && payment.getPaymentStatus().toLowerCase().contains(searchText);
                default:
                    return false;
            }
        });

        paymentTable.setItems(filteredPayments);
    }

    @FXML
    public void clearSearch() {
        searchField.clear();
        paymentTable.setItems(allPayments);
    }

    @FXML
    public void showUpdateForm() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a payment to update");
            alert.showAndWait();
            return;
        }

        // Populate the form with the selected payment's details
        updatePaymentIdField.setText(selectedPayment.getPaymentId());
        updateAppointmentIdField.setText(selectedPayment.getAppointmentId());
        updateAmountField.setText(String.valueOf(selectedPayment.getAmount()));
        if (selectedPayment.getPaymentDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            updatePaymentDateField.setText(dateFormat.format(selectedPayment.getPaymentDate()));
        } else {
            updatePaymentDateField.setText("");
        }
        updatePaymentMethodField.setText(selectedPayment.getPaymentMethod());
        updatePaymentStatusComboBox.getSelectionModel().select(selectedPayment.getPaymentStatus());

        // Show the update form
        updateForm.setVisible(true);
        updateForm.setManaged(true);
    }

    @FXML
    public void updatePayment() {
        try {
            String paymentId = updatePaymentIdField.getText();
            String paymentMethod = updatePaymentMethodField.getText().trim();
            String paymentStatus = updatePaymentStatusComboBox.getSelectionModel().getSelectedItem();

            // Validate required fields
            if (paymentMethod.isEmpty() || paymentStatus == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all required fields");
                alert.showAndWait();
                return;
            }

            // Update the payment
            paymentService.updatePayment(paymentId, paymentMethod, paymentStatus);

            // Refresh the table
            loadPayments();

            // Hide the update form
            updateForm.setVisible(false);
            updateForm.setManaged(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment updated successfully");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating payment: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelUpdate() {
        // Hide the update form and clear fields
        updateForm.setVisible(false);
        updateForm.setManaged(false);
        updatePaymentIdField.clear();
        updateAppointmentIdField.clear();
        updateAmountField.clear();
        updatePaymentDateField.clear();
        updatePaymentMethodField.clear();
        updatePaymentStatusComboBox.getSelectionModel().clearSelection();
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