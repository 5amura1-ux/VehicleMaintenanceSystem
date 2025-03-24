package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.AuditLog;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface AuditLogDAO {
    void addAuditLog(AuditLog auditLog);
    AuditLog getAuditLog(String logId);
    List<AuditLog> getAllAuditLogs();
    void updateAuditLog(AuditLog auditLog);
    void deleteAuditLog(String logId);

    // Implementation
    class Impl implements AuditLogDAO {
        @Override
        public void addAuditLog(AuditLog auditLog) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(auditLog);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public AuditLog getAuditLog(String logId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(AuditLog.class, logId);
            }
        }

        @Override
        public List<AuditLog> getAllAuditLogs() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<AuditLog> query = session.createQuery("FROM AuditLog", AuditLog.class);
                return query.list();
            }
        }

        @Override
        public void updateAuditLog(AuditLog auditLog) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(auditLog);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteAuditLog(String logId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                AuditLog auditLog = session.get(AuditLog.class, logId);
                if (auditLog != null) session.delete(auditLog);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}