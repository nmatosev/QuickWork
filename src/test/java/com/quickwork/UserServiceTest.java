package com.quickwork;

import com.quickwork.dtos.AdChatResponse;
import com.quickwork.dtos.AdResponse;
import com.quickwork.dtos.MessageRequest;
import com.quickwork.dtos.ReviewResponse;
import com.quickwork.dtos.UserResponse;
import com.quickwork.model.Ad;
import com.quickwork.model.RegistrationRequest;
import com.quickwork.model.Review;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@DirtiesContext
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
        RegistrationRequest receiver = new RegistrationRequest(DUMMY_USER2, "pass", "user2@gmail.com", "user", "098555556");
        registrationService.register(sender);
        registrationService.register(receiver);

        User user = userService.getUserByUsername(DUMMY_USER1);

        Ad ad = TestUtils.createAd(user);

        adDAO.save(ad);
        MessageRequest messageRequest = TestUtils.createMessage(ad);
        userService.insertMessage(messageRequest);

        Review review = TestUtils.insertReview(userDAO);
        reviewDAO.save(review);
    }

    @Test
    public void adUsersMessagesTest() {

        Map<Long, AdChatResponse> messages = userService.getUsersAdMessages(DUMMY_USER2);
        Assertions.assertFalse(messages.isEmpty());
        Assertions.assertEquals(1, messages.size());
        AdChatResponse adChatResponse = messages.values().stream().findFirst().get();
        Assertions.assertEquals(1, adChatResponse.getMessages().size());
        Assertions.assertEquals("test ad", adChatResponse.getContent());
        Assertions.assertEquals("user1", adChatResponse.getMessages().get(0).getUser1());
        Assertions.assertEquals("msg-content-test", adChatResponse.getMessages().get(0).getMessageContent());

    }

    @Test
    public void activeAdsTest() {
        List<AdResponse> adDtos = userService.getActiveAds();
        Assertions.assertFalse(adDtos.isEmpty());
        Assertions.assertEquals(1, adDtos.size());
        AdResponse testAd = adDtos.get(0);
        UserResponse userResponse = testAd.getUser();
        Assertions.assertEquals("test ad", testAd.getContent());
        Assertions.assertEquals("test ad title", testAd.getTitle());
        Assertions.assertEquals("user1", testAd.getUser().getUsername());
        Assertions.assertEquals("user1@gmail.com", testAd.getUser().getEmail());
        Assertions.assertEquals("4,00", userResponse.getRating());
    }

    @Test
    public void insertReviewTest() {
        List<ReviewResponse> reviews = userService.getReviewsByUsername(DUMMY_USER1);
        Assertions.assertFalse(reviews.isEmpty());
        ReviewResponse reviewResponse = reviews.stream().findFirst().get();
        Assertions.assertEquals("review-title", reviewResponse.getTitle());
        Assertions.assertEquals("review-content", reviewResponse.getContent());
        Assertions.assertEquals(4, reviewResponse.getRating());
    }

    @Test
    public void getUsersTest() {
        List<UserResponse> users = userService.getUsers();
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertEquals(2, users.size());
    }

    @AfterEach
    public void cleanup() {
        reviewDAO.deleteAll();
        messageDAO.deleteAll();
        adDAO.deleteAll();
        userDAO.deleteAll();
    }


}
