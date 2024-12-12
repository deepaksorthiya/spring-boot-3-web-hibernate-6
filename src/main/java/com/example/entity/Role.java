package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(
        name = "ROLES",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_ROLES_ROLE_NAME", columnNames = {"roleName"})
        })
@NamedEntityGraphs(@NamedEntityGraph(name = "graph.Role.permissions", attributeNodes = @NamedAttributeNode("permissions")))
public class Role {

    @Id
    @GeneratedValue
    private Long roleId;

    private String roleName;

    private String roleDesc;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<AppUser> appUsers = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "ROLES_PERMISSIONS_MAPPING",
            joinColumns = @JoinColumn(name = "ROLE_ID", foreignKey = @ForeignKey(name = "FK_ROLES_PERMISSIONS_MAPPING_ROLES_ROLE_ID")), inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID", foreignKey = @ForeignKey(name = "FK_ROLES_PERMISSIONS_MAPPING_PERMISSIONS_PERMISSION_ID")),
            uniqueConstraints = {
                    @UniqueConstraint(name = "UQ_ROLES_PERMISSIONS_MAPPING_USER_ID_ROLE_ID", columnNames = {"ROLE_ID", "PERMISSION_ID"}),
            }
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role(String roleName, String roleDesc) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
        permission.getRoles().add(this);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
        permission.getRoles().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roleId);
    }
}
