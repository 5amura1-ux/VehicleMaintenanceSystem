package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.PaymentDAO;
import com.oscars.vehiclemaintenancesystem.model.Payment;

import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public String processPayment(String appointmentId, String paymentMethod) throws Exception {
        return paymentDAO.insertPayment(appointmentId, paymentMethod);
    }

    public void deletePayment(String paymentId) throws Exception {
        paymentDAO.deletePayment(paymentId);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }
}