package com.quickwork.controller;

import com.quickwork.dtos.*;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.User;
import com.quickwork.dtos.AdResponse;
import com.quickwork.dtos.ProfilePictureResponse;
import com.quickwork.service.UserService;
import com.quickwork.utilities.Endpoints;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
//@RequestMapping("/api")
public class UserController {
    protected final Log logger = LogFactory.getLog(this.getClass());


    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService actionService, ModelMapper modelMapper) {
        this.userService = actionService;
        this.modelMapper = modelMapper;
    }

    @ApiOperation(value = "Auth", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/public/authenticate")
    public String authenticate() {
        return userService.getUsers().toString();
    }

    @ApiOperation(value = "Retrieve all users data from DB", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/public" + Endpoints.USERS)
    public List<UserResponse> getAllUsers() {
        return userService.getUsers();
    }

    @ApiOperation(value = "Retrieve all users data from DB by ID", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = Endpoints.USER + "/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(modelMapper.map(user, UserResponse.class), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve all users data from DB by username", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "user/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable("username") String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(modelMapper.map(user, UserResponse.class), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve all active ads by user", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "ads/{username}")
    public List<AdResponse> getUsersAds(@PathVariable("username") String username) {
        List<Ad> ads = userService.getActiveAdsByUsername(username);
        return ads.stream().map(e -> modelMapper.map(e, AdResponse.class)).collect(Collectors.toList());
    }

    @ApiOperation(value = "Retrieve all messages for user", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "public/{username}")
    public List<AdChatResponse> getUsersMessages(@PathVariable("username") String username) {
        logger.info(userService.getUsersAdMessages(username).values());
        return new ArrayList<>(userService.getUsersAdMessages(username).values());
    }

    @ApiOperation(value = "Retrieve all active ads", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "public/ads")
    public List<AdResponse> getAds() {
        return userService.getActiveAds();
    }

    @ApiOperation(value = "Insert ad", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "public/ad")
    public ResponseEntity<String> insertAd(@RequestBody AdRequest adRequest) {
        logger.info("Inserting new ad");
        userService.insertAd(adRequest);
        return new ResponseEntity<>("Ad inserted successfully!", HttpStatus.CREATED);
    }

    //TODO disable this api for non registered users
    @ApiOperation(value = "Save review", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "public/review")
    public ResponseEntity<String> insertReview(@RequestBody ReviewRequest reviewRequest) {
        logger.info("Inserting new review");
        userService.insertReview(reviewRequest);
        return new ResponseEntity<>("Review inserted successfully!", HttpStatus.CREATED);
    }

    //TODO disable this api for non registered users
    @ApiOperation(value = "Send message", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "public/message")
    public ResponseEntity<String> sendMessage(@RequestBody MessageRequest messageRequest) {
        logger.info("Inserting new message");
        userService.insertMessage(messageRequest);
        return new ResponseEntity<>("Message inserted successfully!", HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @ApiOperation(value = "Get profile picture", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "public/profile-picture")
    public ProfilePictureResponse getProfilePicture(@RequestBody ProfilePictureRequest profilePictureRequest) {
        return userService.getProfilePicture(profilePictureRequest.getUsername());
    }


    @ApiOperation(value = "Upload profile picture")
    @PostMapping(value = "public/upload", headers = ("content-type=multipart/*"), consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadProfilePic(@RequestParam("imageFile") MultipartFile file, HttpServletResponse httpServletResponse)
            throws IOException {
        logger.info("Uploading new profile picture for user " + file.getResource().getFilename());
        userService.setProfilePicture(file.getResource().getFilename(), file);
        return new ResponseEntity<>("pic uploaded successfully!", HttpStatus.OK);
    }

    @ApiOperation(value = "Delete ad", produces = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping(value = "public/ad/{id}")
    public ResponseEntity<String> deleteAd(@PathVariable("id") long id) {
        userService.deleteAd(id);
        return new ResponseEntity<>("Ad deleted successfully!", HttpStatus.OK);
    }

    @ApiOperation(value = "Get all counties", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "public/counties")
    public List<County> getCounties() {
        return userService.getCounties();
    }

    @ApiOperation(value = "Get all reviews for user", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = "review/{username}")
    public List<ReviewResponse> getReviews(@PathVariable String username) {
        return userService.getReviewsByUsername(username);
    }

}
