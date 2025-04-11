package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;

@Entity
@Table(name = "Roles", schema = "OSCARS")
public class Role {
    @Id
    @Column(name = "role_id")
    private String roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "description")
    private String description;

    // Default constructor
    public Role() {}

    // Parameterized constructor
    public Role(String roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
    }

    // Getters and Setters
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}