package com.quickwork;

import com.quickwork.model.RegistrationRequest;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.RegistrationService;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class RegistrationTest  {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserDAO userDAO;

    @Container
    public static PostgreSQLContainer container = new PostgreSQLContainer()
            .withUsername("test").withPassword("test").withDatabaseName("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);

    }

    private RegistrationRequest prepareRegistrationRequest() {
        return new RegistrationRequest("test", "pass", "user@gmail.com", "user", "098555555");
    }


    @Test
    public void registerUser() {
        RegistrationRequest request = prepareRegistrationRequest();
        String response = registrationService.register(request);
        Assertions.assertEquals("User test successfully registered", response);
    }

    @Test
    public void registerUserWithTheSameData() {
        RegistrationRequest request = prepareRegistrationRequest();
        registrationService.register(request);
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> registrationService.register(request));

        Assertions.assertEquals("User with that username or email already exists!", exception.getMessage());
    }

    @AfterEach
    public void cleanup() {
        userDAO.deleteAll();
    }

}
