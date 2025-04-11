package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.controller.LoginController;
import com.oscars.vehiclemaintenancesystem.dao.RoleDAO;
import com.oscars.vehiclemaintenancesystem.model.Role;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

public class RoleService {
    private final RoleDAO roleDAO = new RoleDAO();

    public String addRole(String roleName) throws Exception {
        return roleDAO.insertRole(roleName);
    }

    public String createRole(String roleName, String description) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection conn = session.doReturningWork(connection -> connection);

            // Get the logged-in user's user_id
            String userId = LoginController.getLoggedInUser();
            if (userId == null) {
                throw new IllegalStateException("No user is logged in");
            }

            // Set the user_id in the database context
            CallableStatement setContextStmt = conn.prepareCall("{call OSCARS.SET_APP_CONTEXT(?)}");
            setContextStmt.setString(1, userId);
            setContextStmt.execute();
            setContextStmt.close();

            CallableStatement stmt = conn.prepareCall("{call OSCARS.INSERT_ROLE(?, ?, ?)}");
            stmt.setString(1, roleName);
            stmt.setString(2, description);
            stmt.registerOutParameter(3, Types.VARCHAR); // For p_role_id

            stmt.execute();

            String roleId = stmt.getString(3);
            session.getTransaction().commit();
            return roleId;
        } catch (Exception e) {
            throw new RuntimeException("Error creating role: " + e.getMessage(), e);
        }
    }

    public void updateRole(String roleId, String roleName, String description) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection conn = session.doReturningWork(connection -> connection);

            // Get the logged-in user's user_id
            String userId = LoginController.getLoggedInUser();
            if (userId == null) {
                throw new IllegalStateException("No user is logged in");
            }

            System.out.println("Setting context with user_id: " + userId);

            // Set the user_id in the database context
            CallableStatement setContextStmt = conn.prepareCall("{call OSCARS.SET_APP_CONTEXT(?)}");
            setContextStmt.setString(1, userId);
            setContextStmt.execute();
            setContextStmt.close();

            System.out.println("Context set, calling UPDATE_ROLE for role_id: " + roleId);

            CallableStatement stmt = conn.prepareCall("{call OSCARS.UPDATE_ROLE(?, ?, ?)}");
            stmt.setString(1, roleId);
            stmt.setString(2, roleName);
            stmt.setString(3, description);

            stmt.execute();
            session.getTransaction().commit();

            System.out.println("Role updated successfully: " + roleId);
        } catch (Exception e) {
            System.err.println("Error in updateRole: " + e.getMessage());
            throw new RuntimeException("Error updating role: " + e.getMessage(), e);
        }
    }

    public void deleteRole(String roleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection conn = session.doReturningWork(connection -> connection);

            // Get the logged-in user's user_id
            String userId = LoginController.getLoggedInUser();
            if (userId == null) {
                throw new IllegalStateException("No user is logged in");
            }

            // Set the user_id in the database context
            CallableStatement setContextStmt = conn.prepareCall("{call OSCARS.SET_APP_CONTEXT(?)}");
            setContextStmt.setString(1, userId);
            setContextStmt.execute();
            setContextStmt.close();

            CallableStatement stmt = conn.prepareCall("{call OSCARS.DELETE_ROLE(?)}");
            stmt.setString(1, roleId);

            stmt.execute();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting role: " + e.getMessage(), e);
        }
    }

    public List<Role> getAllRoles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Role> query = session.createQuery("FROM Role", Role.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving roles: " + e.getMessage(), e);
        }
    }

    public Role getRoleById(String roleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Role.class, roleId);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving role by ID " + roleId + ": " + e.getMessage(), e);
        }
    }
}