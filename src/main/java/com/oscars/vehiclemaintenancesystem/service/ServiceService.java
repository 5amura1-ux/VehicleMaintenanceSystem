package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.ServiceDAO;
import com.oscars.vehiclemaintenancesystem.model.Service;

import java.util.List;

public class ServiceService {
    private final ServiceDAO serviceDAO = new ServiceDAO();

    public String addService(String categoryId, String serviceName, String description, double baseCost, int estimatedTime) throws Exception {
        return serviceDAO.insertService(categoryId, serviceName, description, baseCost, estimatedTime);
    }

    public void deleteService(String serviceId) throws Exception {
        serviceDAO.deleteService(serviceId);
    }

    public List<Service> getAllServices() {
        return serviceDAO.getAllServices();
    }
}