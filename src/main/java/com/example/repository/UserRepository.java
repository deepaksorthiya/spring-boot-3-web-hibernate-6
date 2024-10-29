package com.example.repository;

import com.example.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long>, CustomUserRepo {

    String USER_AND_ROLE = "SELECT u FROM AppUser u LEFT JOIN FETCH u.roles r";
    String GET_SINGLE_USER_WITH_ROLE = "SELECT u FROM AppUser u LEFT JOIN FETCH u.roles r WHERE u.userId=:userId";
    String COUNT_USER_AND_ROLE = "SELECT COUNT(*) FROM AppUser u LEFT JOIN u.roles r";

    String USER_AND_ROLE_AND_PERMISSION = "SELECT u FROM AppUser u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions p";
    String COUNT_USER_AND_ROLE_AND_PERMISSION = "SELECT COUNT(*) FROM AppUser u LEFT JOIN u.roles r LEFT JOIN r.permissions p";
    String INNER_COUNT_USER_ROLE_PERMISSION = "SELECT COUNT(*) FROM AppUser u INNER JOIN u.roles r INNER JOIN r.permissions p WHERE u.userId IN(1,2)";

    @Query(value = USER_AND_ROLE, countQuery = COUNT_USER_AND_ROLE)
    Page<AppUser> getAllUserWithRoles(Pageable pageable);

    @Query(value = USER_AND_ROLE_AND_PERMISSION, countQuery = COUNT_USER_AND_ROLE_AND_PERMISSION)
    Page<AppUser> getAllUserWithRolesAndPermissions(Pageable pageable);

    @Query(value = INNER_COUNT_USER_ROLE_PERMISSION)
    long getAllCountUserWithRolesAndPermissions();

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Page<AppUser> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"roles"})
    Page<AppUser> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<AppUser> findByUserId(Long userId);

    @Query(GET_SINGLE_USER_WITH_ROLE)
    Optional<AppUser> getUserWithRoles(@Param("userId") Long userId);

    Page<AppUser> findByRolesRoleId(Long roleId, Pageable pageable);
}
