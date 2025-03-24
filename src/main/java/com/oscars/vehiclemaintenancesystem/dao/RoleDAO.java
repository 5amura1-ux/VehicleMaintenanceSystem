package com.oscars.vehiclemaintenancesystem.dao;

import com.oscars.vehiclemaintenancesystem.entity.Role;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public interface RoleDAO {
    void addRole(Role role);
    Role getRole(String roleId);
    List<Role> getAllRoles();
    void updateRole(Role role);
    void deleteRole(String roleId);

    // Implementation
    class Impl implements RoleDAO {
        @Override
        public void addRole(Role role) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.save(role);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public Role getRole(String roleId) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                return session.get(Role.class, roleId);
            }
        }

        @Override
        public List<Role> getAllRoles() {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Role> query = session.createQuery("FROM Role", Role.class);
                return query.list();
            }
        }

        @Override
        public void updateRole(Role role) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.update(role);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }

        @Override
        public void deleteRole(String roleId) {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                Role role = session.get(Role.class, roleId);
                if (role != null) session.delete(role);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
        }
    }
}