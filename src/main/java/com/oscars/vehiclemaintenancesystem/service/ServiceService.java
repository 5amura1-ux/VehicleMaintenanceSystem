package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.ServiceDAO;
import com.oscars.vehiclemaintenancesystem.model.Service;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Service> query = session.createQuery("FROM Service", Service.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving services: " + e.getMessage());
        }
    }

}