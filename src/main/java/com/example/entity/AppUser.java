package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
        name = "APP_USERS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_APP_USERS_EMAIL", columnNames = {"email"})
        })
public class AppUser {

    @Id
    @GeneratedValue
    private Long userId;

    @Email(message = "{appuser.email.not.valid}")
    @NotBlank(message = "{appuser.email.not.empty}")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "{appuser.fname.not.empty}")
    private String firstName;

    @NotBlank(message = "{appuser.lname.not.empty}")
    private String lastName;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "APP_USERS_ROLES_MAPPING",
            joinColumns = @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_APP_USERS_ROLES_MAPPING_APP_USERS_USER_ID")), inverseJoinColumns = @JoinColumn(name = "ROLE_ID", foreignKey = @ForeignKey(name = "FK_APP_USERS_ROLES_MAPPING_ROLES_ROLE_ID")),
            uniqueConstraints = {
                    @UniqueConstraint(name = "UQ_APP_USERS_ROLES_MAPPING_APP_USER_ID_ROLE_ID", columnNames = {"USER_ID", "ROLE_ID"}),
            }
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
        role.getAppUsers().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getAppUsers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(userId, appUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}