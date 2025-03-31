package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.ServiceCategory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ServiceCategoryDAO {
    public String insertServiceCategory(String categoryName, String description) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            ServiceCategory category = new ServiceCategory();
            category.setCategoryName(categoryName);
            category.setDescription(description);
            session.save(category);
            tx.commit();
            return category.getCategoryId();
        }
    }

    public void deleteServiceCategory(String categoryId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            ServiceCategory category = session.get(ServiceCategory.class, categoryId);
            if (category != null) {
                session.delete(category);
            }
            tx.commit();
        }
    }

    public List<ServiceCategory> getAllServiceCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ServiceCategory", ServiceCategory.class).list();
        }
    }
}