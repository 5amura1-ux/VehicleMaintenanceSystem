package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.UserDAO;
import com.oscars.vehiclemaintenancesystem.model.User;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public String addUser(String username, String password, String roleId, String firstName, String lastName, String email) throws Exception {
        return userDAO.insertUser(username, password, roleId, firstName, lastName, email);
    }

    public void deactivateUser(String userId) throws Exception {
        userDAO.deactivateUser(userId);
    }

        public void updateUser(User user) throws Exception {
        userDAO.updateUser(user);
    }

    public List<User> getUsersByRole(String roleId) throws Exception {
        return userDAO.getUsersByRole(roleId);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserByUsername(String Username) {
        return (User) userDAO.getUserByUsername(Username);
    }

    public User updateUserProfile(String userId, String text, String text1, String text2, String newPassword) {
        return userDAO.updateUserProfile(userId, text, text1, text2, newPassword);
    }

    // Add method to get a user by username
//    public User getUserByUsername(String username) throws Exception {
//        return userDAO.getUserByUsername(username);
//    }
//
//    // Add method to update a user's profile
//    public void updateUserProfile(String userId, String firstName, String lastName, String email, String newPassword) throws Exception {
//        userDAO.updateUserProfile(userId, firstName, lastName, email, newPassword);
//    }
}