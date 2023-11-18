package com.example;

import com.example.entity.AppUser;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.util.*;

@Component
@Slf4j
public class SpringCommandLineRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final RequestMappingInfoHandlerMapping requestMappingInfoHandlerMapping;

    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper;

    private final ApplicationContext applicationContext;


    public SpringCommandLineRunner(UserRepository userRepository, RoleRepository roleRepository,
                                   PermissionRepository permissionRepository,
                                   RequestMappingInfoHandlerMapping requestMappingInfoHandlerMapping, ObjectMapper objectMapper, ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.requestMappingInfoHandlerMapping = requestMappingInfoHandlerMapping;
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {

        log.info("Entity Manager : {} ", entityManager);
        log.info("Permission Repo : {} ", permissionRepository);
        log.info("Role Repo : {} ", roleRepository);
        Map<RequestMappingInfo, HandlerMethod> handlersMethod = requestMappingInfoHandlerMapping.getHandlerMethods();

        handlersMethod.forEach((key, value) -> log.info(key.getPathPatternsCondition().getPatternValues() + " : " + key.getMethodsCondition().getMethods()));

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlersMethod.entrySet()) {
            System.out.println(entry.getKey());
        }

        Set<Object> moduleIds = objectMapper.getRegisteredModuleIds();
        log.info("Modules : {} ", moduleIds);

        String[] names1 = applicationContext.getBeanNamesForType(Validator.class);
        String[] names2 = applicationContext.getBeanNamesForType(org.springframework.validation.Validator.class);

        log.info("Jakarta Validator Beans : {} ", Arrays.toString(names1));
        log.info("Spring Validator Beans : {} ", Arrays.toString(names2));

        Permission permission = new Permission(1, "permission", "permission desc", null);
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);
        Role role = new Role(1, "role", "role desc", null, permissions);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        permission.setRoles(roles);

        // Role saveRole = roleRepository.save(role);

        List<AppUser> appUserList = new ArrayList<>();

        for (int i = 0; i < 26; i++) {
            char c = (char) (i + 97);
            AppUser appUser = new AppUser(0, c + "@gmail.com", c + "", c + "", null);
            appUserList.add(appUser);
        }
        List<AppUser> saveAppUserList = userRepository.saveAll(appUserList);
        for (AppUser u : saveAppUserList) {
            u.setRoles(roles);
            userRepository.save(u);
        }
    }

}
