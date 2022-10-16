package com.quickwork.service;

import com.quickwork.dtos.*;
import com.quickwork.model.*;
import com.quickwork.dtos.AdResponse;
import com.quickwork.dtos.ProfilePictureResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {

    User getUserById(long id);

    User getUserByUsername(String username);

    List<UserResponse> getUsers();

    List<Ad> getActiveAdsByUsername(String username);

    List<AdResponse> getActiveAds();

    void insertAd(AdRequest adRequest);

    void deleteAd(long id);

    List<ReviewResponse> getReviewsByUsername(String username);

    List<County> getCounties();

    void insertReview(ReviewRequest reviewRequest);

    void insertMessage(MessageRequest messageDto);

    Map<Long, AdChatResponse> getUsersAdMessages(String username);

    void setProfilePicture(String username, MultipartFile file) throws IOException;

    void setProfilePicture(ImageRequest imageRequest) throws IOException;

   ProfilePictureResponse getProfilePicture(String username);
}
