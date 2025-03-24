package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.ErrorLog;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface ErrorLogDAO {
    void addErrorLog(ErrorLog errorLog);
    ErrorLog getErrorLog(String errorId);
    List<ErrorLog> getAllErrorLogs();
    void updateErrorLog(ErrorLog errorLog);
    void deleteErrorLog(String errorId);

    // Implementation
    class Impl implements ErrorLogDAO {
        @Override
        public void addErrorLog(ErrorLog errorLog) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(errorLog);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public ErrorLog getErrorLog(String errorId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(ErrorLog.class, errorId);
            }
        }

        @Override
        public List<ErrorLog> getAllErrorLogs() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<ErrorLog> query = session.createQuery("FROM ErrorLog", ErrorLog.class);
                return query.list();
            }
        }

        @Override
        public void updateErrorLog(ErrorLog errorLog) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(errorLog);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteErrorLog(String errorId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                ErrorLog errorLog = session.get(ErrorLog.class, errorId);
                if (errorLog != null) session.delete(errorLog);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}