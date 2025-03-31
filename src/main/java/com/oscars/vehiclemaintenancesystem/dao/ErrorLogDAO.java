package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.ErrorLog;
import org.hibernate.Session;

import java.util.List;

public class ErrorLogDAO {
    public List<ErrorLog> getAllErrorLogs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ErrorLog", ErrorLog.class).list();
        }
    }
}