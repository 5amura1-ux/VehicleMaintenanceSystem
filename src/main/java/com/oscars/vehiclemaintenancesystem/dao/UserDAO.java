package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class UserDAO {
    public String insertUser(String username, String password, String roleId, String firstName, String lastName, String email) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_USER(?,?,?,?,?,?,?)}");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, roleId);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            stmt.setString(6, email);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.execute();
            String userId = stmt.getString(7);
            tx.commit();
            return userId;
        }
    }

    public void deactivateUser(String userId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call MAKE_THE_USER_INACTIVE(?)}");
            stmt.setString(1, userId);
            stmt.execute();
            tx.commit();
        }
    }

    public void updateUser(User user) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(user);
            tx.commit();
        }
    }


    public List<User> getUsersByRole(String roleId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE roleId = :roleId", User.class)
                    .setParameter("roleId", roleId)
                    .list();
        }
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public User getUserByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.byId(User.class).load(username);
            tx.commit();
        }
        return user;
    }

    public void updateUserProfile(String userId, String firstName, String lastName, String email, String newPassword) {
    }
}