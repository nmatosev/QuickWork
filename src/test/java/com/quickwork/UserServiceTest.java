package com.quickwork;

import com.quickwork.dtos.AdChatResponse;
import com.quickwork.dtos.AdResponse;
import com.quickwork.dtos.MessageRequest;
import com.quickwork.dtos.ReviewResponse;
import com.quickwork.dtos.UserResponse;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.RegistrationRequest;
import com.quickwork.model.Review;
import com.quickwork.model.User;
import com.quickwork.repository.AdDAO;
import com.quickwork.repository.CountyDAO;
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
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class UserServiceTest extends AbstractTest {

    @Autowired
    protected WebApplicationContext context;

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

    @Autowired
    private CountyDAO countyDAO;

    public static final String DUMMY_USER1 = "user1";
    public static final String DUMMY_USER2 = "user2";

    @BeforeEach
    public void prepareScenarios() {
        messageDAO.deleteAll();
        reviewDAO.deleteAll();
        adDAO.deleteAll();
        userDAO.deleteAll();
        countyDAO.deleteAll();

        County county = new County(1L, "1", "Zagrebacka");
        countyDAO.save(county);
        List<County> countyList = countyDAO.findAll();

        User user = TestUtils.createUser(TestUtils.DUMMY_USER1);
        User user2 = TestUtils.createUser(TestUtils.DUMMY_USER2);
        userDAO.save(user);
        userDAO.save(user2);
        Ad ad = TestUtils.createAd(user, countyList.get(0));
        adDAO.save(ad);

        Review review = TestUtils.insertReview(userDAO);
        reviewDAO.save(review);

        MessageRequest messageRequest = TestUtils.createMessage(ad);
        userService.insertMessage(messageRequest);

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
        Assertions.assertEquals("adtest", testAd.getTitle());
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

/*    @Test
    public void testSetProfilePictureTest () {
        MultipartFile multipartFile = new MockMultipartFile();
        userService.setProfilePicture();
    }*/

    @Test
    public void getUsersTest() {
        List<UserResponse> users = userService.getUsers();
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertEquals(2, users.size());
    }

    @AfterEach
    public void cleanup() {

    }


}
