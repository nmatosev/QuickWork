package com.quickwork.service;

import com.quickwork.dtos.AdDto;
import com.quickwork.dtos.ReviewDto;
import com.quickwork.dtos.UserDto;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.Review;
import com.quickwork.model.User;

import java.util.List;

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

}
