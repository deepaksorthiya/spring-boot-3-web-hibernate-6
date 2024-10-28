package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@ToString
@Entity
@NamedEntityGraphs(@NamedEntityGraph(name = "graph.Role.permissions", attributeNodes = @NamedAttributeNode("permissions")))
public class Role implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    private String roleName;

    private String roleDesc;

    @ManyToMany(mappedBy = "roles", targetEntity = AppUser.class)
    @JsonIgnore
    private Set<AppUser> appUsers;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, targetEntity = Permission.class)
    @JoinTable(name = "ROLE_PERMISSION_MAPPING")
    private Set<Permission> permissions;

}
