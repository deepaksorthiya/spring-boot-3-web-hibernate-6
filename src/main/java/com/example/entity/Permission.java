package com.example.entity;

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
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_permissions_permission_name", columnNames = {"permission_name"})
        })
public class Permission {

    @Id
    @GeneratedValue
    private Long permissionId;

    private String permissionName;

    private String permissionDesc;

    @ToString.Exclude
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    public Permission(String permissionName, String permissionDesc) {
        this.permissionName = permissionName;
        this.permissionDesc = permissionDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(permissionId);
    }
}
