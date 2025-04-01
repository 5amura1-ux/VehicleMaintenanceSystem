package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.CustomerDAO;
import com.oscars.vehiclemaintenancesystem.model.Customer;

import java.util.List;

public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public String addCustomer(String firstName, String lastName, String phoneNumber, String email, String address) throws Exception {
        return customerDAO.insertCustomer(firstName, lastName, phoneNumber, email, address);
    }

    public void deleteCustomer(String customerId) throws Exception {
        customerDAO.deleteCustomer(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
}