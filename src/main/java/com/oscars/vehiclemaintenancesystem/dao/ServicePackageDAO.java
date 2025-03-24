package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.ServicePackage;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface ServicePackageDAO {
    void addServicePackage(ServicePackage servicePackage);
    ServicePackage getServicePackage(String packageId);
    List<ServicePackage> getAllServicePackages();
    void updateServicePackage(ServicePackage servicePackage);
    void deleteServicePackage(String packageId);

    // Implementation
    class Impl implements ServicePackageDAO {
        @Override
        public void addServicePackage(ServicePackage servicePackage) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(servicePackage);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public ServicePackage getServicePackage(String packageId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(ServicePackage.class, packageId);
            }
        }

        @Override
        public List<ServicePackage> getAllServicePackages() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<ServicePackage> query = session.createQuery("FROM ServicePackage", ServicePackage.class);
                return query.list();
            }
        }

        @Override
        public void updateServicePackage(ServicePackage servicePackage) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(servicePackage);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteServicePackage(String packageId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                ServicePackage servicePackage = session.get(ServicePackage.class, packageId);
                if (servicePackage != null) session.delete(servicePackage);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}