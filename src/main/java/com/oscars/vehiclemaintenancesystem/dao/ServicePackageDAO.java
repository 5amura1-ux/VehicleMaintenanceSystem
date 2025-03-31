package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.ServicePackage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ServicePackageDAO {
    public String insertServicePackage(String packageName, String description, double discountPrice) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            ServicePackage servicePackage = new ServicePackage();
            servicePackage.setPackageName(packageName);
            servicePackage.setDescription(description);
            servicePackage.setDiscountPrice(discountPrice);
            session.save(servicePackage);
            tx.commit();
            return servicePackage.getPackageId();
        }
    }

    public void deleteServicePackage(String packageId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            ServicePackage servicePackage = session.get(ServicePackage.class, packageId);
            if (servicePackage != null) {
                session.delete(servicePackage);
            }
            tx.commit();
        }
    }

    public List<ServicePackage> getAllServicePackages() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ServicePackage", ServicePackage.class).list();
        }
    }
}