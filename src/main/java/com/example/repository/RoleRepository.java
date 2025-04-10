package com.example.repository;

import com.example.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @EntityGraph(attributePaths = {"permissions"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT r from Role r")
    Page<Role> getRoleWithPermissions(Pageable pageable);

    @EntityGraph(attributePaths = {"permissions"})
    Optional<Role> findByRoleId(Long roleId);

    Page<Role> findByPermissionsPermissionId(Long permissionId, Pageable pageable);

    Set<Role> findByRoleIdIn(Set<Long> roleIds);
}
