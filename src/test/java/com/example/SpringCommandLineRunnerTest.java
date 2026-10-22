package com.example;

import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringCommandLineRunnerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RequestMappingInfoHandlerMapping requestMappingHandlerMapping;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MessageSource messageSource;

    @Mock
    private EntityManager entityManager;

    private SpringCommandLineRunner runner;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        runner = new SpringCommandLineRunner(
                userRepository,
                roleRepository,
                permissionRepository,
                requestMappingHandlerMapping,
                objectMapper,
                applicationContext,
                messageSource
        );
    }

    @Test
    @DisplayName("Should execute run method and invoke saveTestUserData")
    void testRun() {
        ReflectionTestUtils.setField(runner, "entityManager", entityManager);

        RequestMappingInfo mappingInfo = mock(RequestMappingInfo.class);
        PathPatternsRequestCondition pathPatternsCondition = mock(PathPatternsRequestCondition.class);
        RequestMethodsRequestCondition methodsCondition = mock(RequestMethodsRequestCondition.class);
        HandlerMethod handlerMethod = mock(HandlerMethod.class);

        when(mappingInfo.getPathPatternsCondition()).thenReturn(pathPatternsCondition);
        when(mappingInfo.getMethodsCondition()).thenReturn(methodsCondition);
        when(pathPatternsCondition.getPatternValues()).thenReturn(Set.of("/api/test"));
        when(methodsCondition.getMethods()).thenReturn(Set.of());

        when(requestMappingHandlerMapping.getHandlerMethods()).thenReturn(Map.of(mappingInfo, handlerMethod));
        when(objectMapper.getRegisteredModuleIds()).thenReturn(Set.of("module1"));
        when(applicationContext.getBeanNamesForType(Validator.class)).thenReturn(new String[]{"validator1"});
        when(applicationContext.getBeanNamesForType(org.springframework.validation.Validator.class)).thenReturn(new String[]{"springValidator1"});
        when(messageSource.getMessage(eq("appuser.not.found"), any(Object[].class), any(Locale.class)))
                .thenReturn("Resource not found");

        runner.run("arg1");

        verify(userRepository, times(1)).saveTestUserData();
        verify(requestMappingHandlerMapping, times(1)).getHandlerMethods();
        verify(objectMapper, times(1)).getRegisteredModuleIds();
        verify(messageSource, times(1)).getMessage(eq("appuser.not.found"), any(Object[].class), any(Locale.class));
    }
}
