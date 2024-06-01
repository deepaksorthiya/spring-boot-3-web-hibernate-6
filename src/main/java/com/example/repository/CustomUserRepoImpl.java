package com.example.repository;

import com.example.entity.AppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class CustomUserRepoImpl implements CustomUserRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AppUser> getAllUsersByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AppUser> cr = cb.createQuery(AppUser.class);
        Root<AppUser> root = cr.from(AppUser.class);
        cr.select(root).where(cb.like(root.get("email"), "%" + email + "%"));

        Query query = entityManager.createQuery(cr);
        List<AppUser> results = query.getResultList();
        return results;
    }
}
