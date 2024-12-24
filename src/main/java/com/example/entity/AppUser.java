package com.example.entity;

import com.example.validation.PasswordMatching;
import com.example.validation.StrongPassword;
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
        name = "app_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_app_users_email", columnNames = {"email"})
        })
@PasswordMatching(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "{appuser.password.match}"
)
public class AppUser {

    @Id
    @GeneratedValue
    private Long userId;

    @Email(message = "{appuser.email.not.valid}")
    @NotBlank(message = "{appuser.email.not.empty}")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "{appuser.password.not.empty}")
    @StrongPassword(message = "{appuser.password.strong}")
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private String confirmPassword;

    @NotBlank(message = "{appuser.fname.not.empty}")
    private String firstName;

    @NotBlank(message = "{appuser.lname.not.empty}")
    private String lastName;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "app_users_roles_mapping",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_app_users_roles_mapping_user_id_app_users_user_id")), inverseJoinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_app_users_roles_mapping_role_id_roles_role_id")),
            uniqueConstraints = {
                    @UniqueConstraint(name = "UQ_app_users_roles_mapping_user_id_role_id", columnNames = {"user_id", "role_id"}),
            }
    )
    private Set<Role> roles = new HashSet<>();

    public AppUser(String email, String password, String confirmPassword, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstName = firstName;
        this.lastName = lastName;
    }

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