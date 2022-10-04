package com.quickwork;

import com.quickwork.dtos.MessageRequest;
import com.quickwork.dtos.ReviewDto;
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

    public static Ad createAd(User user) {
        Ad ad = new Ad();
        ad.setUser(user);
        ad.setId(0);
        ad.setCounty(new County(1, "Z", "Zagrebacka"));
        Date weekAfter = new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 7));
        ad.setValidUntil(weekAfter);
        ad.setContent("test ad");
        ad.setTitle("test ad title");
        return ad;
    }

    public static MessageRequest createMessage(Ad ad) {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessageContent("msg-content-test");
        messageRequest.setSender(DUMMY_USER1);
        messageRequest.setReceiver(DUMMY_USER2);
        messageRequest.setAdId(ad.getId());
        return messageRequest;
    }

    public static Review insertReview(UserDAO userDAO) {
        ReviewDto reviewDto = new ReviewDto(1L, "review-title", "review-content", 4, DUMMY_USER2, DUMMY_USER1);
        Review review = new Review();
        Optional<User> user = userDAO.findByUsername(reviewDto.getReviewedUsername());
        if (user.isPresent()) {
            review.setUser(user.get());
            review.setTitle(reviewDto.getTitle());
            review.setContent(reviewDto.getContent());
            review.setRating(reviewDto.getRating());
            review.setRole(RoleCode.USER.name());
        } else {
            throw new NotFoundException("User with username " + reviewDto.getReviewedUsername() + " not found!");
        }

        return review;
    }
}
