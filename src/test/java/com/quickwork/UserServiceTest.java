package com.quickwork;

import com.quickwork.dtos.AdChat;
import com.quickwork.dtos.AdDto;
import com.quickwork.dtos.MessageRequest;
import com.quickwork.dtos.UserDto;
import com.quickwork.model.Ad;
import com.quickwork.model.RegistrationRequest;
import com.quickwork.model.User;
import com.quickwork.repository.AdDAO;
import com.quickwork.repository.MessageDAO;
import com.quickwork.repository.ReviewDAO;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.RegistrationService;
import com.quickwork.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class UserServiceTest extends AbstractTest {
    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AdDAO adDAO;

    @Autowired
    private ReviewDAO reviewDAO;

    public static final String DUMMY_USER1 = "user1";
    public static final String DUMMY_USER2 = "user2";

    @BeforeEach
    public void prepareScenarios() {
        RegistrationRequest sender = new RegistrationRequest(DUMMY_USER1, "pass", "user1@gmail.com", "user", "098555555");
        RegistrationRequest receiver = new RegistrationRequest(DUMMY_USER2, "pass", "user2@gmail.com", "user", "098555555");
        registrationService.register(sender);
        registrationService.register(receiver);

        User user = userService.getUserByUsername(DUMMY_USER1);
        Ad ad = TestUtils.createAd(user);

        adDAO.save(ad);
        MessageRequest messageRequest = TestUtils.createMessage(ad);
        userService.insertMessage(messageRequest);

        TestUtils.insertReview(userService);
    }

    @Test
    public void AdUsersMessagesTest() {

        Map<Long, AdChat> messages = userService.getUsersAdMessages(DUMMY_USER1);
        Assertions.assertFalse(messages.isEmpty());
        Assertions.assertEquals(1, messages.get(1L).getMessages().size());
        Assertions.assertEquals("test ad", messages.get(1L).getContent());
        Assertions.assertEquals("user1", messages.get(1L).getMessages().get(0).getUser1());
        Assertions.assertEquals("msg-content-test", messages.get(1L).getMessages().get(0).getMessageContent());

    }

    @Test
    public void activeAdsTest() {
        List<AdDto> adDtos = userService.getActiveAds();
        Assertions.assertFalse(adDtos.isEmpty());
        Assertions.assertEquals(1, adDtos.size());
        AdDto testAd = adDtos.get(0);
        UserDto userDto = testAd.getUser();
        Assertions.assertEquals("test ad", testAd.getContent());
        Assertions.assertEquals("test ad title", testAd.getTitle());
        Assertions.assertEquals("user1", testAd.getUser().getUsername());
        Assertions.assertEquals("Zagrebačka županija", testAd.getCounty());
        Assertions.assertEquals("user1@gmail.com", testAd.getUser().getEmail());
        Assertions.assertEquals("4,00", userDto.getRating());
    }


    @AfterEach
    public void cleanup() {
        reviewDAO.deleteAll();
        messageDAO.deleteAll();
        adDAO.deleteAll();
        userDAO.deleteAll();
    }


}
