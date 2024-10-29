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
        name = "PERMISSIONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_PERMISSIONS_PERMISSION_NAME", columnNames = {"permissionName"})
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
