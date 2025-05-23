package com.example.repository;

import com.example.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    Optional<UserGroup> findByGroupName(String name);

    @Query("SELECT g FROM UserGroup g LEFT JOIN FETCH g.appUsers WHERE g.userGroupId = :groupId")
    Optional<UserGroup> findByIdWithUsers(@Param("groupId") Long groupId);

    @Query("SELECT g FROM UserGroup g LEFT JOIN FETCH g.appUsers")
    List<UserGroup> findAllWithUsers();
}
