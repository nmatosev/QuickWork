package com.quickwork.service;

import com.quickwork.dtos.*;
import com.quickwork.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    Map<Long, AdChat> getUsersAdMessages(String username);

    void setProfilePicture(String username, MultipartFile file) throws IOException;

    void setProfilePicture(ImageRequest imageRequest) throws IOException;

   ProfilePictureDto getProfilePicture(String username);
}
