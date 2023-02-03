package com.quickwork;

import com.quickwork.model.RegistrationRequest;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.RegistrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class RegistrationTest extends AbstractTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserDAO userDAO;


    @Test
    public void registerUser() {
        RegistrationRequest request = TestUtils.prepareRegistrationRequest();
        String response = registrationService.register(request);
        Assertions.assertEquals("User test successfully registered", response);
    }

    @Test
    public void registerUserWithTheSameData() {
        RegistrationRequest request = TestUtils.prepareRegistrationRequest();
        registrationService.register(request);
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> registrationService.register(request));
        Assertions.assertEquals("User with that username or email already exists!", exception.getMessage());
    }

    @AfterEach
    public void cleanup() {
        userDAO.deleteAll();
    }

}
