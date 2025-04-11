package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.UserDAO;
import com.oscars.vehiclemaintenancesystem.model.User;
import com.oscars.vehiclemaintenancesystem.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User getUserByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user by username: " + e.getMessage(), e);
        }
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all users: " + e.getMessage(), e);
        }
    }
    public User validateUser(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult();
            if (user != null) {
                String encryptedPassword = encryptPassword(password);
                if (encryptedPassword.equals(user.getPassword())) {
                    return user; // Passwords match, return the user
                }
            }
            return null; // User not found or password doesn't match
        } catch (Exception e) {
            throw new RuntimeException("Error validating user: " + e.getMessage(), e);
        }
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void createUser(String username, String password, String roleId, String firstName, String lastName, String email, String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            // Get the sequence value as a BigDecimal and convert to Long
            BigDecimal sequenceValue = (BigDecimal) session.createNativeQuery("SELECT seq_user.NEXTVAL FROM DUAL").getSingleResult();
            String userId = "USER" + String.format("%05d", sequenceValue.longValue());
            session.createNativeQuery("INSERT INTO OSCARS.Users (user_id, username, password, role_id, first_name, last_name, email, created_date, status) " +
                            "VALUES (:userId, :username, :password, :roleId, :firstName, :lastName, :email, SYSDATE, :status)")
                    .setParameter("userId", userId)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .setParameter("roleId", roleId)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .setParameter("email", email)
                    .setParameter("status", status)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    public void updateUserProfile(String userId, String firstName, String lastName, String email, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                if (newPassword != null) {
                    user.setPassword(newPassword);
                }
                session.update(user);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating user profile: " + e.getMessage(), e);
        }
    }

    public List<User> getMechanics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE roleId = 'ROLE00003'", User.class);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving mechanics: " + e.getMessage(), e);
        }
    }
    public User getUserById(String userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving user by ID " + userId + ": " + e.getMessage());
        }
    }





    // Add method to get a user by username
}