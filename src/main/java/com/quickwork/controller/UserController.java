package com.quickwork.controller;

import com.quickwork.dtos.AdDto;
import com.quickwork.dtos.UserDto;
import com.quickwork.model.Ad;
import com.quickwork.model.User;
import com.quickwork.service.UserService;
import com.quickwork.utilities.Endpoints;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {


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
    @GetMapping(value = Endpoints.USERS)
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
        return ads.stream().map(e->modelMapper.map(e, AdDto.class)).collect(Collectors.toList());
    }

}
