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
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_roles_role_name", columnNames = {"role_name"})
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
    @JoinTable(name = "roles_permissions_mapping",
            joinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_roles_permissions_mapping_role_id_roles_role_id")), inverseJoinColumns = @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(name = "FK_roles_permissions_mapping_permission_id_permissions_permission_id")),
            uniqueConstraints = {
                    @UniqueConstraint(name = "UQ_roles_permissions_mapping_role_id_permission_id", columnNames = {"role_id", "permission_id"}),
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
        return Objects.equals(this.roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roleId);
    }
}
