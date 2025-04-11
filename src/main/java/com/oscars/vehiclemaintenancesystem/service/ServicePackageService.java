package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.ServicePackageDAO;
import com.oscars.vehiclemaintenancesystem.model.ServicePackage;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ServicePackageService {
    private final ServicePackageDAO servicePackageDAO = new ServicePackageDAO();


    public List<ServicePackage> getAllServicePackages() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServicePackage> query = session.createQuery("FROM ServicePackage", ServicePackage.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving service packages: " + e.getMessage());
        }
    }
}