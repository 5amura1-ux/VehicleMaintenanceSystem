package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.AuditLog;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AuditLogDAO {
    public List<AuditLog> getAllAuditLogs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Use a dynamic native query to fetch from the view
            Query<AuditLog> query = session.createNativeQuery(
                    "SELECT log_id, table_name, action, user_id, user_name, details, timestamp FROM OSCARS.VW_AUDIT_LOG",
                    AuditLog.class
            );
            return query.list();
        } catch (Exception e) {
            System.err.println("Error fetching from VW_AUDIT_LOG in DAO: " + e.getMessage());
            // Fallback to fetching directly from the Audit_Log table
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.createQuery("FROM AuditLog", AuditLog.class).list();
            } catch (Exception fallbackEx) {
                throw new RuntimeException("Error retrieving audit logs: " + fallbackEx.getMessage());
            }
        }
    }
}