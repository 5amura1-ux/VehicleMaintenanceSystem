package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import com.oscars.vehiclemaintenancesystem.model.Role;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class RoleDAO {
    public String insertRole(String roleName) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Connection conn = session.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
                    .getService(org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class).getConnection();
            CallableStatement stmt = conn.prepareCall("{call INSERT_ROLE(?,?)}");
            stmt.setString(1, roleName);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.execute();
            String roleId = stmt.getString(2);
            tx.commit();
            return roleId;
        }
    }

    public void deleteRole(String roleId) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Role role = session.get(Role.class, roleId);
            if (role != null) {
                session.delete(role);
            }
            tx.commit();
        }
    }

    public List<Role> getAllRoles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Role", Role.class).list();
        }
    }
}