package com.quickwork.service;

import com.quickwork.dtos.*;
import com.quickwork.model.*;
import com.quickwork.repository.*;
import com.quickwork.service.exception.NotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
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
    private static final SimpleDateFormat DATE_FORMAT_WEEK_DAY = new SimpleDateFormat("EEE");
    private final ModelMapper modelMapper;

    protected final Log logger = LogFactory.getLog(this.getClass());

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
    public void insertMessage(MessageRequest messageDto) {
        Message message = new Message();
        mapRequestToMessage(message, messageDto);
        messageDAO.save(message);
    }


    private void mapRequestToMessage(Message message, MessageRequest messageRequest) {

        assert messageRequest.getMessageContent() != null;
        Optional<User> user1 = userDAO.findByUsername(messageRequest.getSender());
        Optional<Ad> ad = adDAO.findById(messageRequest.getAdId());


        if (user1.isPresent() && ad.isPresent()) {
            message.setMessage(messageRequest.getMessageContent());
            message.setUser1(user1.get());
            message.setUser2(ad.get().getUser());
            message.setAd(ad.get());
        } else {
            logger.warn("User with username " + messageRequest.getSender() + " or ad with " + messageRequest.getAdId() + " id not found!");
            throw new NotFoundException("User with username " + messageRequest.getSender() + "or ad with " + messageRequest.getAdId() + " not found!");
        }
    }



    public Map<Long, AdChat> getUsersAdMessages(String username) {
        Map<Long, AdChat> adChats = new HashMap<>();
        List<Message> messages = messageDAO.findAll();
        List<MessageDto> messageDtos = new ArrayList<>();
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            for (Message message : messages) {
                //find by receiver/the one who created the ad
                if (message.getUser1().getUsername().equals(username) || message.getUser2().getUsername().equals(username)) {
                    AdChat adChat = new AdChat();
                    adChat.setAdId(message.getAd().getId());
                    adChat.setTitle(message.getAd().getTitle());
                    adChat.setContent(message.getAd().getContent());
                    adChats.put(message.getAd().getId(), adChat);
                }
            }
            mapMessageToDto(messageDtos, messages, username);
            for (MessageDto messageDto : messageDtos) {
                if (adChats.containsKey(messageDto.getAdId())) {
                    //all messages for this ad
                    AdChat adChat = adChats.get(messageDto.getAdId());
                    adChat.getMessages().add(messageDto);
                }
            }
        }
        return adChats;
    }



    private void mapMessageToDto(List<MessageDto> messageDtos, List<Message> messages, String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            //return messages if receiver is found
            for (Message message : messages) {
                MessageDto messageDto = new MessageDto();
                messageDto.setMessageContent(message.getMessage());
                messageDto.setUser1(message.getUser1().getUsername());
                messageDto.setUser2(message.getUser2().getUsername());
                messageDto.setAdId(message.getAd().getId());
                String weekDay = DATE_FORMAT_WEEK_DAY.format(message.getCreatedDate());
                messageDto.setWeekDay(weekDay);
                //messageDto.setAdDto(adDto);
                messageDtos.add(messageDto);

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
