package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.model.AuditLog;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AuditLogService {
    public List<AuditLog> getAllAuditLogs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AuditLog> query = session.createNamedQuery("AuditLog.findAllFromView", AuditLog.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving audit logs: " + e.getMessage());
        }
    }
}