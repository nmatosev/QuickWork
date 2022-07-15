package com.quickwork;

import com.quickwork.dtos.MessageRequest;
import com.quickwork.dtos.ReviewDto;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.RegistrationRequest;
import com.quickwork.model.User;
import com.quickwork.service.UserService;

import java.util.Date;

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

    public static void insertReview(UserService userService) {
        ReviewDto reviewDto = new ReviewDto(1L, "review-title", "review-content", 4 ,DUMMY_USER2,DUMMY_USER1 );
        userService.insertReview(reviewDto);
    }
}
