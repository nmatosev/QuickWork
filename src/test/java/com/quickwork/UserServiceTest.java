package com.quickwork;

import com.quickwork.dtos.AdChat;
import com.quickwork.dtos.MessageRequest;
import com.quickwork.model.RegistrationRequest;
import com.quickwork.repository.MessageDAO;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.RegistrationService;
import com.quickwork.service.UserService;
import org.junit.Assert;
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

import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private MessageDAO messageDAO;

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

    @Test
    public void getMsg() {
        RegistrationRequest request = new RegistrationRequest("sender", "pass", "user@gmail.com", "user", "098555555");
        RegistrationRequest request1 = new RegistrationRequest("receiver", "pass", "receiver@gmail.com", "user", "098555555");
        registrationService.register(request);
        registrationService.register(request1);

        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessageContent("msg-content");
        messageRequest.setReceiver("receiver");
        messageRequest.setSender("sender");
        messageRequest.setAdId(1);
        userService.insertMessage(messageRequest);
        Map<Long, AdChat> messages = userService.getUsersAdMessages("sender");
        Assert.assertFalse(messages.isEmpty());
    }


    @AfterEach
    public void cleanup() {
        userDAO.deleteAll();
        messageDAO.deleteAll();
    }


}
