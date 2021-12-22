package com.quickwork.service;

import com.quickwork.dtos.AdDto;
import com.quickwork.dtos.MessageDto;
import com.quickwork.dtos.ReviewDto;
import com.quickwork.dtos.UserDto;
import com.quickwork.model.*;
import com.quickwork.repository.*;
import com.quickwork.service.exception.NotFoundException;
import liquibase.pro.packaged.M;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    private final CountyDAO countyDAO;
    private final ReviewDAO reviewDAO;
    private final MessageDAO messageDAO;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy.");
    private final ModelMapper modelMapper;


    public UserServiceImpl(UserDAO userDAO, AdDAO adDAO, CountyDAO countyDAO, ReviewDAO reviewDao, MessageDAO messageDAO, ModelMapper modelMapper) {
        this.userDAO = userDAO;
        this.adDAO = adDAO;
        this.countyDAO = countyDAO;
        this.reviewDAO = reviewDao;
        this.messageDAO = messageDAO;
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
    public List<AdDto> getActiveAds() {
        List<Ad> ads = adDAO.findAll();
        Date currentDate = new Date(System.currentTimeMillis());
        List<Ad> activeAds = ads.stream().filter(el -> el.getValidUntil().after(currentDate)).collect(Collectors.toList());
        List<AdDto> activeAdDtos = new ArrayList<>();
        for (Ad ad : activeAds) {
            activeAdDtos.add(mapAdToDto(ad));
        }
        return activeAdDtos;
    }


    private AdDto mapAdToDto(Ad ad) {
        AdDto adDto = new AdDto();
        String validUntil = DATE_FORMAT.format(ad.getValidUntil());
        adDto.setId(ad.getId());
        adDto.setContent(ad.getContent());
        adDto.setTitle(ad.getTitle());
        adDto.setValidUntil(validUntil);
        adDto.setCounty(ad.getCounty().getName());
        adDto.setUser(getUserData(ad.getUser()));
        return adDto;
    }

    private UserDto getUserData(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRating(calculateAverage(user));
        return userDto;
    }

    @Override
    public void insertAd(AdDto adDto) {
        Ad ad = new Ad();
        mapDtoToAd(ad, adDto);
        adDAO.save(ad);
    }

    @Override
    public void deleteAd(long id) {
        Optional<Ad> ad = adDAO.findById(id);
        if (ad.isPresent()) {
            adDAO.deleteById(ad.get().getId());
        } else {
            throw new NotFoundException("Ad with id " + id + " does not exist!");
        }
    }

    private void mapDtoToAd(Ad ad, AdDto adDto) {
        ad.setTitle(adDto.getTitle());
        ad.setContent(adDto.getContent());
        County county = new County();
        county.setId(adDto.getCountyId());
        ad.setCounty(county);
        Date weekAfter = new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 7));
        ad.setValidUntil(weekAfter);
        User user = new User();
        user.setId(adDto.getUserId());
        ad.setUser(user);
    }

    @Override
    public void insertReview(ReviewDto reviewDto) {
        Review review = new Review();
        mapDtoToReview(review, reviewDto);
        reviewDAO.save(review);
    }

    private void mapDtoToReview(Review review, ReviewDto reviewDto) {
        Optional<User> user = userDAO.findByUsername(reviewDto.getReviewedUsername());
        if (user.isPresent()) {
            review.setUser(user.get());
            review.setContent(reviewDto.getContent());
            review.setRating(reviewDto.getRating());
            //TODO fix this
            review.setRole(RoleCode.USER.name());
        } else {
            throw new NotFoundException("User with username " + reviewDto.getReviewedUsername() + " not found!");
        }

    }

    @Override
    public void insertMessage(MessageDto messageDto) {
        Message message = new Message();
        mapDtoToMessage(message, messageDto);
        messageDAO.save(message);
    }


    private void mapDtoToMessage(Message message, MessageDto messageDto) {
        Optional<User> user = userDAO.findByUsername(messageDto.getSender());
        Optional<Ad> ad = adDAO.findById(messageDto.getAdId());
        if (user.isPresent() && ad.isPresent()) {
            message.setMessage(messageDto.getMessageContent());
            message.setUser(user.get());
            message.setAd(ad.get());

        } else {
            throw new NotFoundException("User with username " + messageDto.getSender() + "or ad with " + messageDto.getAdId() + " not found!");
        }
    }

    @Override
    public List<MessageDto> getUsersMessages(String username) {
        List<MessageDto> messageDtos = new ArrayList<>();
        List<Message> messages = messageDAO.findAll();
        mapMessageToDto(messageDtos, messages, username);
        return messageDtos;

    }

    private void mapMessageToDto(List<MessageDto> messageDtos, List<Message> messages, String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            //return messages if receiver is found
            for (Message message : messages) {
                if (message.getAd().getUser().getUsername().equals(username)) {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageContent(message.getMessage());
                    messageDto.setSender(message.getUser().getUsername());
                    messageDto.setAdId(message.getAd().getId());
                    messageDtos.add(messageDto);
                }
            }
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
    public List<County> getCounties() {
        return countyDAO.findAll();
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
        userDto.setPhoneNumber(user.getPhoneNumber());
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
