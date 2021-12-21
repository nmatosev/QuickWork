package com.quickwork.controller;

import com.quickwork.dtos.AdDto;
import com.quickwork.dtos.ReviewDto;
import com.quickwork.dtos.UserDto;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.User;
import com.quickwork.service.UserService;
import com.quickwork.utilities.Endpoints;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping(value = "/authenticate")
    public String authenticate() {
        return userService.getUsers().toString();
    }

    @ApiOperation(value = "Retrieve all users data from DB", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/public" + Endpoints.USERS)
    public List<UserDto> getAllUsers() {
        return userService.getUsers();
        //return users.stream().map(e -> modelMapper.map(e, UserDto.class)).collect(Collectors.toList());
    }

    @ApiOperation(value = "Retrieve all users data from DB by ID", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = Endpoints.USER + "/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(modelMapper.map(user, UserDto.class), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve all users data from DB by username", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "userByUsername/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(modelMapper.map(user, UserDto.class), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve all active ads by user", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "adsByUsername/{username}")
    public List<AdDto> getUsersAds(@PathVariable("username") String username) {
        List<Ad> ads = userService.getActiveAdsByUsername(username);
        return ads.stream().map(e -> modelMapper.map(e, AdDto.class)).collect(Collectors.toList());
    }

    @ApiOperation(value = "Retrieve all active ads", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "public/ads")
    public List<AdDto> getAds() {
        return userService.getActiveAds();
    }

    @ApiOperation(value = "Insert ad", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "public/ad")
    public ResponseEntity<String> insertAd(@RequestBody AdDto adDto) {
        logger.info("Inserting new ad");
        userService.insertAd(adDto);
        return new ResponseEntity<>("Ad inserted successfully!", HttpStatus.CREATED);

    }

    //TODO disable this api for non registered users
    @ApiOperation(value = "Save review", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "public/saveReview")
    public ResponseEntity<String> insertReview(@RequestBody ReviewDto reviewDto) {
        logger.info("Inserting new review");
        userService.insertReview(reviewDto);
        return new ResponseEntity<>("Review inserted successfully!", HttpStatus.CREATED);

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

}
