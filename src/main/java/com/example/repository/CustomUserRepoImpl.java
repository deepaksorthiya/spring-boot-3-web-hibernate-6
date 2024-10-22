package com.example.repository;

import com.example.dto.AppUserDto;
import com.example.entity.AppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.Collections;
import java.util.List;

public class CustomUserRepoImpl implements CustomUserRepo {

    @PersistenceContext
    private EntityManager entityManager;

    private final EntityManagerFactory entityManagerFactory;

    public CustomUserRepoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private static AppUser convert(AppUserDto appUserDto) {
        return new AppUser(appUserDto.userId(), appUserDto.email(), appUserDto.password(), appUserDto.firstName(), appUserDto.lastName(), Collections.emptySet());
    }

    @Override
    public List<AppUser> getAllUsersByEmailUsingEntityManager(String email) {
        long startTime = System.currentTimeMillis();
        // Execute the query
        Query query = entityManager.createQuery("""
                       SELECT new com.example.dto.AppUserDto(u.userId, u.email, u.password, u.firstName, u.lastName)
                       FROM AppUser u where u.email like :email
                """);
        query.setParameter("email", "%" + email + "%");
        // limit query not work for all databases
        query.setMaxResults(10);
        List<AppUserDto> users = query.getResultList();
        entityManager.close();
        long endTime = System.currentTimeMillis();
        System.out.println(users);
        System.out.println("Time taken : " + (endTime - startTime));
        return users.stream().map(CustomUserRepoImpl::convert).toList();
    }

    @Override
    public List<AppUser> getAllUsersByEmailUsingSessionFactory(String email) {
//        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//        final List<AppUser> appUsers = new ArrayList<>();
//        sessionFactory.inSession(session -> {
//            org.hibernate.query.Query<AppUser> query = session.createQuery("from AppUser u left join fetch u.roles", AppUser.class);
//            // not work
//            query.setMaxResults(10);
//            List<AppUser> users = query.list();
//            appUsers.addAll(users);
//        });
        //EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = entityManager.createQuery("from AppUser u join fetch u.roles where u.email like :email");
        query.setParameter("email", "%" + email + "%");
        query.setMaxResults(10); // not work on join fetch
        final List<AppUser> appUsers = query.getResultList();

        //ERROR OUTSIDE of session
        /*
        for (Role role : appUsers.get(0).getRoles()) {
         for (Permission permission : role.getPermissions()) {
         System.out.println(permission);
         }
         }
         */
        return appUsers;

    }
}
