package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.AuditLog;
import org.hibernate.Session;

import java.util.List;

public class AuditLogDAO {
    public List<AuditLog> getAllAuditLogs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM AuditLog", AuditLog.class).list();
        }
    }
}