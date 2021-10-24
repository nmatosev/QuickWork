package com.quickwork.service;

import com.quickwork.dtos.UserDto;
import com.quickwork.model.Ad;
import com.quickwork.model.Review;
import com.quickwork.model.User;
import com.quickwork.repository.AdDAO;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.exception.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final AdDAO adDAO;

    private final ModelMapper modelMapper;


    public UserServiceImpl(UserDAO userDAO, AdDAO adDAO, ModelMapper modelMapper) {
        this.userDAO = userDAO;
        this.adDAO = adDAO;
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
    public List<Ad> getActiveAds() {
        List<Ad> ads = adDAO.findAll();
        Date currentDate = new Date(System.currentTimeMillis());
        return ads.stream().filter(el -> el.getValidUntil().after(currentDate)).collect(Collectors.toList());
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
    public List<UserDto> getUsers() {
        List<User> users = userDAO.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = new UserDto();
            mapToDto(user, userDto);

            userDtos.add(userDto);
        }
        return userDtos;
    }

    private void mapToDto(User user, UserDto userDto) {
        userDto.setUsername(user.getUsername());
        userDto.setRating(calculateAverage(user));
        userDto.setEmail(user.getEmail());
    }

    private String calculateAverage(User user) {
        double sum = 0.0;
        for (Review review : user.getReviews()) {
            sum += review.getRating();
        }
        if (sum > 0.0) {
            double avg = sum / user.getReviews().size();
            return String.format("%.2f", avg);
        } else {
            return "-";
        }
    }

}
