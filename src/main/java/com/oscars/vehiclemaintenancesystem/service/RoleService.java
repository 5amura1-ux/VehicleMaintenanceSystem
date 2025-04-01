package com.oscars.vehiclemaintenancesystem.service;

import com.oscars.vehiclemaintenancesystem.dao.RoleDAO;
import com.oscars.vehiclemaintenancesystem.model.Role;

import java.util.List;

public class RoleService {
    private final RoleDAO roleDAO = new RoleDAO();

    public String addRole(String roleName) throws Exception {
        return roleDAO.insertRole(roleName);
    }

    public void deleteRole(String roleId) throws Exception {
        roleDAO.deleteRole(roleId);
    }

    public List<Role> getAllRoles() {
        return roleDAO.getAllRoles();
    }
}