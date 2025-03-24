package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.ServicePackageDetail;
import com.oscars.vehiclemaintenancesystem.entity.ServicePackageDetailId;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface ServicePackageDetailDAO {
    void addServicePackageDetail(ServicePackageDetail detail);
    ServicePackageDetail getServicePackageDetail(String packageId, String serviceId);
    List<ServicePackageDetail> getAllServicePackageDetails();
    void updateServicePackageDetail(ServicePackageDetail detail);
    void deleteServicePackageDetail(String packageId, String serviceId);

    // Implementation
    class Impl implements ServicePackageDetailDAO {
        @Override
        public void addServicePackageDetail(ServicePackageDetail detail) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(detail);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public ServicePackageDetail getServicePackageDetail(String packageId, String serviceId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(ServicePackageDetail.class, new ServicePackageDetailId(packageId, serviceId));
            }
        }

        @Override
        public List<ServicePackageDetail> getAllServicePackageDetails() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<ServicePackageDetail> query = session.createQuery("FROM ServicePackageDetail", ServicePackageDetail.class);
                return query.list();
            }
        }

        @Override
        public void updateServicePackageDetail(ServicePackageDetail detail) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(detail);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteServicePackageDetail(String packageId, String serviceId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                ServicePackageDetail detail = session.get(ServicePackageDetail.class, new ServicePackageDetailId(packageId, serviceId));
                if (detail != null) session.delete(detail);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}