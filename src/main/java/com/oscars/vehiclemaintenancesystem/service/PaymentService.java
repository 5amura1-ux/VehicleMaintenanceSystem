package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.PaymentDAO;
import com.oscars.vehiclemaintenancesystem.model.Payment;

import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO = new PaymentDAO();


    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

}