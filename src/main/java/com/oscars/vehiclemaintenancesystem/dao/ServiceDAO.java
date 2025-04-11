package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Service;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class ServiceDAO {
    public String insertService(String categoryId, String serviceName, String description, double baseCost, int estimatedTime) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_SERVICE(?,?,?,?,?,?)}");
            stmt.setString(1, categoryId);
            stmt.setString(2, serviceName);
            stmt.setString(3, description);
            stmt.setDouble(4, baseCost);
            stmt.setInt(5, estimatedTime);
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.execute();
            String serviceId = stmt.getString(6);
            tx.commit();
            return serviceId;
        }
    }

    public void deleteService(String serviceId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Service service = session.get(Service.class, serviceId);
            if (service != null) {
                session.delete(service);
            }
            tx.commit();
        }
    }

}