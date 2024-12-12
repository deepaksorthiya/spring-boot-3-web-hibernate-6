package com.example;

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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    private final MessageSource messageSource;


    public SpringCommandLineRunner(UserRepository userRepository, RoleRepository roleRepository,
                                   PermissionRepository permissionRepository,
                                   RequestMappingInfoHandlerMapping requestMappingHandlerMapping, ObjectMapper objectMapper, ApplicationContext applicationContext, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.requestMappingInfoHandlerMapping = requestMappingHandlerMapping;
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
        this.messageSource = messageSource;
    }

    @Override
    public void run(String... args) {
        saveTestUserData();

        log.info("Entity Manager : {} ", entityManager);
        log.info("Permission Repo : {} ", permissionRepository);
        log.info("Role Repo : {} ", roleRepository);
        Map<RequestMappingInfo, HandlerMethod> handlersMethod = requestMappingInfoHandlerMapping.getHandlerMethods();

        handlersMethod.forEach((key, value) -> {
            assert key.getPathPatternsCondition() != null;
            log.info("{} : {}", key.getPathPatternsCondition().getPatternValues(), key.getMethodsCondition().getMethods());
        });

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlersMethod.entrySet()) {
            log.info("{}", entry.getKey());
        }

        Set<Object> moduleIds = objectMapper.getRegisteredModuleIds();
        log.info("Modules SIZE : {} ", moduleIds.size());
        log.info("Modules : {} ", moduleIds);

        String[] names1 = applicationContext.getBeanNamesForType(Validator.class);
        String[] names2 = applicationContext.getBeanNamesForType(org.springframework.validation.Validator.class);
        log.info("#######################################################");
        log.info("Jakarta Validator Beans : {} ", Arrays.toString(names1));
        log.info("Spring Validator Beans : {} ", Arrays.toString(names2));
        log.info("#######################################################");
        String msg = messageSource.getMessage("appuser.not.found", new Object[]{1}, Locale.getDefault());
        log.info("SPRING MSG : {} ", msg);
    }

    private void saveTestUserData() {
        userRepository.saveTestUserData();
    }

}
