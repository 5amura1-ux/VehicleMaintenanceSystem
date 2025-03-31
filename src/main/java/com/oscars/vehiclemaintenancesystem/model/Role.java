package com.oscars.vehiclemaintenancesystem.model;

import javax.persistence.*;

@Entity
@Table(name = "Roles")
public class Role {
    @Id
    @Column(name = "role_id")
    private String roleId;

    @Column(name = "role_name")
    private String roleName;

    // Getters and setters
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}