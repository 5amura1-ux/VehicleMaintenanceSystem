package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.PaymentDAO;
import com.oscars.vehiclemaintenancesystem.model.Payment;

import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO = new PaymentDAO();

//    private final PaymentDAO paymentDAO = new PaymentDAO();

    public String processPayment(String appointmentId, String paymentMethod) {
        return paymentDAO.processPayment(appointmentId, paymentMethod);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    public Payment getPaymentById(String paymentId) {
        return paymentDAO.getPaymentById(paymentId);
    }

    public void deletePayment(String paymentId) {
        paymentDAO.deletePayment(paymentId);
    }

    public void updatePayment(String paymentId, String paymentMethod, String paymentStatus) {
        paymentDAO.updatePayment(paymentId, paymentMethod, paymentStatus);
    }
}