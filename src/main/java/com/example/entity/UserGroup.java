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
@Table(name = "users_group")
public class UserGroup {

    @Id
    @GeneratedValue
    private Long userGroupId;
    private String groupName;
    private String description;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = AppUser.class)
    @JoinTable(name = "users_group_app_users_mapping",
            joinColumns = @JoinColumn(name = "user_group_id", foreignKey = @ForeignKey(name = "FK_users_group_app_users_mapping_user_group_id")),
            inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_users_group_app_users_mapping_user_id"))
    )
    private Set<AppUser> appUsers = new HashSet<>();

    public UserGroup(String groupName, String description) {
        this.groupName = groupName;
        this.description = description;
    }

    // Helper methods for bidirectional relationship management
    public void addAppUser(AppUser appUser) {
        this.appUsers.add(appUser);
        appUser.getUserGroups().add(this);
    }

    public void removeAppUser(AppUser appUser) {
        this.appUsers.remove(appUser);
        appUser.getUserGroups().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGroup userGroup = (UserGroup) o;
        return Objects.equals(userGroupId, userGroup.userGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userGroupId);
    }
}