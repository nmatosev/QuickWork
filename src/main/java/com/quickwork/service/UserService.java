package com.quickwork.service;

import com.quickwork.dtos.*;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.Review;
import com.quickwork.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    void insertUser(UserDto user);

    User getUserById(long id);

    User getUserByUsername(String username);

    void deleteUser(long id);

    List<UserDto> getUsers();

    List<Ad> getActiveAdsByUsername(String username);

    List<AdDto> getActiveAds();

    void insertAd(AdDto adDto);

    void deleteAd(long id);

    List<Review> getReviewsByUsername(String username);

    List<County> getCounties();

    void insertReview(ReviewDto reviewDto);

    void insertMessage(MessageRequest messageDto);

    List<MessageDto> getUsersMessages(String username);

    Map<Long, AdMessages> getUsersAdMessages(String username);
}
