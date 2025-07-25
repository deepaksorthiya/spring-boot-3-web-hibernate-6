package com.example.repository;

import com.example.dto.AppUserDto;
import com.example.entity.AppUser;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.UserGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
public class CustomUserRepoImpl implements CustomUserRepo {

    @PersistenceContext
    private EntityManager entityManager;

    private final EntityManagerFactory entityManagerFactory;

    public CustomUserRepoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private static AppUser convert(AppUserDto appUserDto) {
        return new AppUser(appUserDto.userId(), appUserDto.email(), appUserDto.password(), appUserDto.password(), appUserDto.firstName(), appUserDto.lastName(), Collections.emptySet(), Collections.emptySet());
    }

    @Override
    public List<AppUser> getAllUsersByEmailUsingEntityManager(String email, int start, int max) {
        long startTime = System.currentTimeMillis();
        // pagination will be done in in-memory when using join fetch for below query
//        Query query = entityManager.createQuery("""
//                       SELECT u
//                       FROM AppUser u
//                       join fetch u.roles
//                       where u.email like :email
//                """);
        // Execute the query
        Query query = entityManager.createQuery("""
                       SELECT new com.example.dto.AppUserDto(u.userId, u.email, u.password, u.firstName, u.lastName)
                       FROM AppUser u
                       where u.email like :email
                """);
        query.setParameter("email", "%" + email + "%");
        // limit query not work for all databases
        query.setMaxResults(max);
        query.setFirstResult(start);
        List<AppUserDto> users = query.getResultList();
        entityManager.close();
        long endTime = System.currentTimeMillis();
        log.info("USERS: {}", users);
        log.info("Time taken: {}", (endTime - startTime));
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

    @Transactional
    public void saveTestUserData() {

        // Create groups
        UserGroup adminGroup = new UserGroup("Administrators", "System administrators");
        UserGroup userGroup = new UserGroup("Users", "Regular users");
        UserGroup guestGroup = new UserGroup("Guests", "Guest users with limited access");

        entityManager.persist(adminGroup);
        entityManager.persist(userGroup);
        entityManager.persist(guestGroup);

        log.info("Saving test user data started");
        Permission read = new Permission("READ", "Read only permission");
        Permission write = new Permission("WRITE", "Write permission");

        entityManager.persist(read);
        entityManager.persist(write);

        Role userRole = new Role("ROLE_USER", "User Role Description");
        Role adminRole = new Role("ROLE_ADMIN", "Admin Role Description");

        userRole.addPermission(read);

        adminRole.addPermission(read);
        adminRole.addPermission(write);

        entityManager.persist(userRole);
        entityManager.persist(adminRole);

        for (int i = 1; i <= 26; i++) {
            char c = (char) (i + 96);
            AppUser appUser = new AppUser(c + "user@gmail.com", c + "Password@123", c + "Password@123", c + "firstName", c + "lastName");
            if (i % 2 == 0) {
                appUser.addRole(adminRole);
                // override email to admin
                appUser.setEmail(c + "admin@gmail.com");
                appUser.addToGroup(adminGroup);
            } else if (i % 3 == 0) {
                appUser.addRole(userRole);
                appUser.addToGroup(userGroup);
            } else {
                appUser.addRole(userRole);
                appUser.addToGroup(guestGroup);
            }
            entityManager.persist(appUser);
        }
        log.info("Saving test user data completed");
    }
}
