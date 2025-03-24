package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Service;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface ServiceDAO {
    void addService(Service service);
    Service getService(String serviceId);
    List<Service> getAllServices();
    void updateService(Service service);
    void deleteService(String serviceId);

    // Implementation
    class Impl implements ServiceDAO {
        @Override
        public void addService(Service service) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(service);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Service getService(String serviceId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Service.class, serviceId);
            }
        }

        @Override
        public List<Service> getAllServices() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Service> query = session.createQuery("FROM Service", Service.class);
                return query.list();
            }
        }

        @Override
        public void updateService(Service service) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(service);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteService(String serviceId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Service service = session.get(Service.class, serviceId);
                if (service != null) session.delete(service);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}