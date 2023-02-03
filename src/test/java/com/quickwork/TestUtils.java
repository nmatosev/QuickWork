package com.quickwork;

import com.quickwork.dtos.MessageRequest;
import com.quickwork.dtos.ReviewResponse;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.RegistrationRequest;
import com.quickwork.model.Review;
import com.quickwork.model.RoleCode;
import com.quickwork.model.User;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.exception.NotFoundException;

import java.util.Date;
import java.util.Optional;

public class TestUtils {

    public static final String DUMMY_USER1 = "user1";
    public static final String DUMMY_USER2 = "user2";

    public static RegistrationRequest prepareRegistrationRequest() {
        return new RegistrationRequest("test", "pass", "user@gmail.com", "user", "098555555");
    }

    public static Ad createAd(User user, County county) {
        Ad ad = new Ad();
        ad.setUser(user);
        //ad.setId(1);
        ad.setCounty(county);
        Date weekAfter = new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 7));
        ad.setValidUntil(weekAfter);
        ad.setContent("test ad");
        ad.setTitle("adtest");
        return ad;
    }

    public static MessageRequest createMessage(Ad ad) {
        return new MessageRequest("msg-content-test", DUMMY_USER1, DUMMY_USER2, ad.getId());
    }

    public static User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@gmail.com");
        user.setPassword("xxxx");
        user.setRole("1");
        user.setPhoneNumber("098664646");
        return user;
    }


    public static Review insertReview(UserDAO userDAO) {
        ReviewResponse reviewResponse = new ReviewResponse(1L, "review-title", "review-content", 4, DUMMY_USER2, DUMMY_USER1);
        Review review = new Review();
        Optional<User> user = userDAO.findByUsername(reviewResponse.getReviewedUsername());
        if (user.isPresent()) {
            review.setUser(user.get());
            review.setTitle(reviewResponse.getTitle());
            review.setContent(reviewResponse.getContent());
            review.setRating(reviewResponse.getRating());
            review.setRole(RoleCode.USER.name());
        } else {
            throw new NotFoundException("User with username " + reviewResponse.getReviewedUsername() + " not found!");
        }

        return review;
    }
}
