package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.ServiceCategory;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface ServiceCategoryDAO {
    void addServiceCategory(ServiceCategory category);
    ServiceCategory getServiceCategory(String categoryId);
    List<ServiceCategory> getAllServiceCategories();
    void updateServiceCategory(ServiceCategory category);
    void deleteServiceCategory(String categoryId);

    // Implementation
    class Impl implements ServiceCategoryDAO {
        @Override
        public void addServiceCategory(ServiceCategory category) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(category);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public ServiceCategory getServiceCategory(String categoryId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(ServiceCategory.class, categoryId);
            }
        }

        @Override
        public List<ServiceCategory> getAllServiceCategories() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<ServiceCategory> query = session.createQuery("FROM ServiceCategory", ServiceCategory.class);
                return query.list();
            }
        }

        @Override
        public void updateServiceCategory(ServiceCategory category) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(category);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteServiceCategory(String categoryId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                ServiceCategory category = session.get(ServiceCategory.class, categoryId);
                if (category != null) session.delete(category);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}