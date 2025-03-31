package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.ServiceCategoryDAO;
import com.oscars.vehiclemaintenancesystem.model.ServiceCategory;

import java.util.List;

public class ServiceCategoryService {
    private ServiceCategoryDAO serviceCategoryDAO = new ServiceCategoryDAO();

    public String addServiceCategory(String categoryName, String description) throws Exception {
        return serviceCategoryDAO.insertServiceCategory(categoryName, description);
    }

    public void deleteServiceCategory(String categoryId) throws Exception {
        serviceCategoryDAO.deleteServiceCategory(categoryId);
    }

    public List<ServiceCategory> getAllServiceCategories() {
        return serviceCategoryDAO.getAllServiceCategories();
    }
}