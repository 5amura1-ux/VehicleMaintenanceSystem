package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.ServicePackageDAO;
import com.oscars.vehiclemaintenancesystem.model.ServicePackage;

import java.util.List;

public class ServicePackageService {
    private final ServicePackageDAO servicePackageDAO = new ServicePackageDAO();

    public String addServicePackage(String packageName, String description, double discountPrice) throws Exception {
        return servicePackageDAO.insertServicePackage(packageName, description, discountPrice);
    }

    public void deleteServicePackage(String packageId) throws Exception {
        servicePackageDAO.deleteServicePackage(packageId);
    }

    public List<ServicePackage> getAllServicePackages() {
        return servicePackageDAO.getAllServicePackages();
    }
}