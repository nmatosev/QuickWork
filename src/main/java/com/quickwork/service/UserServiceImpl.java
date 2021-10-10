package com.quickwork.service;

import com.quickwork.dtos.UserDto;
import com.quickwork.model.Ad;
import com.quickwork.model.Review;
import com.quickwork.model.User;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.exception.NotFoundException;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides service for interacting with user JPA repositories.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final ModelMapper modelMapper;


    public UserServiceImpl(UserDAO userDAO, ModelMapper modelMapper) {
        this.userDAO = userDAO;
        this.modelMapper = modelMapper;
    }

    public void insertUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        userDAO.save(user);
    }

    @Override
    public User getUserById(long id) {
        return userDAO.getById(id);
    }

    public User findById(long id) {
        return userDAO.findById(id).orElseThrow(() -> new NotFoundException("User by id " + id + " not found"));
    }


    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User with username " + username + " not found!");
        }
    }

    @Override
    public List<Ad> getActiveAdsByUsername(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        Date currentDate = new Date(System.currentTimeMillis());
        if (user.isPresent()) {
            return user.get().getAds().stream().filter(el -> el.getValidUntil().after(currentDate)).collect(Collectors.toList());
        } else {
            throw new NotFoundException("User with username " + username + " not found!");
        }
    }

    @Override
    public List<Review> getReviewsByUsername(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            return user.get().getReviews();
        } else {
            throw new NotFoundException("User with username " + username + " not found!");
        }
    }


    @Override
    public void deleteUser(long id) {
        Optional<User> user = userDAO.findById(id);
        if (user.isPresent()) {
            userDAO.deleteById(id);
        } else {
            throw new NotFoundException("User with ID " + id + " not found!");
        }
    }

    @Override
    public List<User> getUsers() {
        return userDAO.findAll();
    }

}
