package com.quickwork.service;

import com.quickwork.dtos.*;
import com.quickwork.model.*;
import com.quickwork.repository.*;
import com.quickwork.dtos.AdResponse;
import com.quickwork.dtos.MessageResponse;
import com.quickwork.dtos.ProfilePictureResponse;
import com.quickwork.service.exception.NotFoundException;
import com.quickwork.utilities.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
    private final ProfilePicDAO profilePicDAO;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy.");
    private static final SimpleDateFormat DATE_FORMAT_WEEK_DAY = new SimpleDateFormat("EEE");
    private final ModelMapper modelMapper;

    protected final Log logger = LogFactory.getLog(this.getClass());

    public UserServiceImpl(UserDAO userDAO, AdDAO adDAO, CountyDAO countyDAO, ReviewDAO reviewDao, MessageDAO messageDAO, ProfilePicDAO profilePicDAO, ModelMapper modelMapper) {
        this.userDAO = userDAO;
        this.adDAO = adDAO;
        this.countyDAO = countyDAO;
        this.reviewDAO = reviewDao;
        this.messageDAO = messageDAO;
        this.profilePicDAO = profilePicDAO;
        this.modelMapper = modelMapper;
    }

    public void insertUser(UserResponse userResponse) {
        User user = modelMapper.map(userResponse, User.class);
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
    public List<AdResponse> getActiveAds() {
        List<Ad> ads = adDAO.findAll();
        Date currentDate = new Date(System.currentTimeMillis());
        List<Ad> activeAds = ads.stream().filter(el -> el.getValidUntil().after(currentDate)).collect(Collectors.toList());
        List<AdResponse> activeAdResponses = new ArrayList<>();
        for (Ad ad : activeAds) {
            activeAdResponses.add(mapToAddResponses(ad));
        }
        return activeAdResponses;
    }


    private AdResponse mapToAddResponses(Ad ad) {
        AdResponse adDto = new AdResponse();
        String validUntil = DATE_FORMAT.format(ad.getValidUntil());
        adDto.setId(ad.getId());
        adDto.setContent(ad.getContent());
        adDto.setTitle(ad.getTitle());
        adDto.setValidUntil(validUntil);
        adDto.setCounty(ad.getCounty().getName());
        adDto.setUser(getUserData(ad.getUser()));
        return adDto;
    }

    private UserResponse getUserData(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setRating(calculateAverage(user));
        return userResponse;
    }

    @Override
    public void insertAd(AdRequest adRequest) {
        Ad ad = new Ad();
        mapDtoToAd(ad, adRequest);
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

    private void mapDtoToAd(Ad ad, AdRequest adRequest) {
        ad.setTitle(adRequest.getTitle());
        ad.setContent(adRequest.getContent());
        County county = new County();
        county.setId(adRequest.getCountyId());
        ad.setCounty(county);
        Date expiryDate = new Date(new Date().getTime() + Constants.EXPIRY_DATE_OFFSET);
        ad.setValidUntil(expiryDate);
        User user = new User();
        user.setId(adRequest.getUserId());
        ad.setUser(user);
    }

    @Override
    public void insertReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        mapDtoToReview(review, reviewRequest);
        reviewDAO.save(review);
    }

    private void mapDtoToReview(Review review, ReviewRequest reviewRequest) {
        Optional<User> user = userDAO.findByUsername(reviewRequest.getReviewedUsername());
        if (user.isPresent()) {
            review.setUser(user.get());
            review.setContent(reviewRequest.getContent());
            review.setRating(reviewRequest.getRating());
            //TODO fix this
            review.setRole(RoleCode.USER.name());
        } else {
            throw new NotFoundException("User with username " + reviewRequest.getReviewedUsername() + " not found!");
        }
    }

    @Override
    public void insertMessage(MessageRequest messageDto) {
        Message message = new Message();
        if (messageDto.getReceiver() == null) {
            mapRequestToMessageModal(message, messageDto);
        } else {
            mapRequestToMessageChat(message, messageDto);
        }
        messageDAO.save(message);
    }


    private void mapRequestToMessageModal(Message message, MessageRequest messageRequest) {

        assert messageRequest.getMessageContent() != null;
        Optional<User> user1 = userDAO.findByUsername(messageRequest.getSender());
        Optional<Ad> ad = adDAO.findById(messageRequest.getAdId());
        //if chat is initiated
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

    private void mapRequestToMessageChat(Message message, MessageRequest messageRequest) {

        assert messageRequest.getMessageContent() != null;
        Optional<User> user1 = userDAO.findByUsername(messageRequest.getSender());
        Optional<User> user2 = userDAO.findByUsername(messageRequest.getReceiver());
        Optional<Ad> ad = adDAO.findById(messageRequest.getAdId());

        if (user1.isPresent() && user2.isPresent() && ad.isPresent()) {
            //if ad owner sends message
            message.setMessage(messageRequest.getMessageContent());
            message.setUser1(user1.get());
            message.setUser2(user2.get());
            message.setAd(ad.get());
        } else if (user1.isPresent() && user2.isEmpty() && ad.isPresent()) {
            //if chat initator sends message
            message.setMessage(messageRequest.getMessageContent());
            message.setUser1(user1.get());
            message.setUser2(ad.get().getUser());
            message.setAd(ad.get());
        } else {
            logger.warn("User with username " + messageRequest.getSender() + " or ad with " + messageRequest.getAdId() + " id not found!");
            throw new NotFoundException("User with username " + messageRequest.getSender() + "or ad with " + messageRequest.getAdId() + " not found!");
        }
    }


    public Map<Long, AdChatResponse> getUsersAdMessages(String username) {
        Map<Long, AdChatResponse> adChats = new HashMap<>();
        List<Message> messages = messageDAO.findAll();
        List<MessageResponse> messageDtos = new ArrayList<>();
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            for (Message message : messages) {
                //find by receiver/the one who created the ad
                if (message.getUser1().getUsername().equals(username) || message.getUser2().getUsername().equals(username)) {
                    AdChatResponse adChatResponse = new AdChatResponse();
                    adChatResponse.setAdId(message.getAd().getId());
                    adChatResponse.setTitle(message.getAd().getTitle());
                    adChatResponse.setContent(message.getAd().getContent());
                    adChats.put(message.getAd().getId(), adChatResponse);
                }
            }
            mapMessageToDto(messageDtos, messages, username);
            for (MessageResponse messageDto : messageDtos) {
                if (adChats.containsKey(messageDto.getAdId())) {
                    //all messages for this ad
                    AdChatResponse adChatResponse = adChats.get(messageDto.getAdId());
                    adChatResponse.getMessages().add(messageDto);
                }
            }
        }
        return adChats;
    }


    private void mapMessageToDto(List<MessageResponse> messageResponses, List<Message> messages, String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            //return messages if receiver is found
            for (Message message : messages) {
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setMessageContent(message.getMessage());
                messageResponse.setUser1(message.getUser1().getUsername());
                messageResponse.setUser2(message.getUser2().getUsername());
                messageResponse.setAdId(message.getAd().getId());
                String weekDay = DATE_FORMAT_WEEK_DAY.format(message.getCreatedDate());
                messageResponse.setWeekDay(weekDay);
                //messageDto.setAdDto(adDto);
                messageResponses.add(messageResponse);
            }
        } else {
            throw new NotFoundException("User with username " + username + " not found!");
        }
    }


    @Override
    public List<ReviewResponse> getReviewsByUsername(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            List<ReviewResponse> reviewResponses = new ArrayList<>();
            for (Review review : user.get().getReviews()) {
                ReviewResponse reviewResponse = new ReviewResponse();
                reviewResponse.setReviewedUsername(review.getUser().getUsername());//TODO check this
                reviewResponse.setContent(review.getContent());
                reviewResponse.setRating(review.getRating());
                reviewResponse.setTitle(review.getTitle());
                reviewResponses.add(reviewResponse);
            }

            return reviewResponses;
        } else {
            throw new NotFoundException("User with username " + username + " not found!");
        }
    }

    @Override
    public List<County> getCounties() {
        return countyDAO.findAll();
    }


    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userDAO.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserResponse userResponse = new UserResponse();
            mapToDto(user, userResponse);

            userResponses.add(userResponse);
        }
        return userResponses;
    }

    private void mapToDto(User user, UserResponse userResponse) {
        userResponse.setUsername(user.getUsername());
        userResponse.setRating(calculateAverage(user));
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());
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

    @Override
    public void setProfilePicture(String username, MultipartFile file) throws IOException {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            byte[] byteArr = file.getBytes();
            //InputStream inputStream = new ByteArrayInputStream(byteArr);
            ProfilePicture profilePic = new ProfilePicture();
            profilePic.setName(file.getOriginalFilename());
            profilePic.setUser(user.get());
            profilePic.setEncodedPicture(compressBytes(byteArr));

            profilePicDAO.save(profilePic);
        }
    }

    @Override
    public ProfilePictureResponse getProfilePicture(String username) {
        Optional<ProfilePicture> user = profilePicDAO.findByName(username);
        logger.info("Getting profile pic for " + username );
        if(user.isPresent()) {
            ProfilePicture profilePic = user.get();
            profilePic.setEncodedPicture(decompressBytes(profilePic.getEncodedPicture()));
            return mapToResponse(profilePic);
        }
        logger.info("Profile picture for user " + username + " not found");

        return null;
    }

    private ProfilePictureResponse mapToResponse(ProfilePicture profilePic) {
        ProfilePictureResponse profilePictureResponse = new ProfilePictureResponse();
        StringBuilder base64 = new StringBuilder("data:image/png;base64,");
        base64.append(Base64.getEncoder().encodeToString(profilePic.getEncodedPicture()));
        profilePictureResponse.setEncodedPicture(base64.toString());
        return profilePictureResponse;
    }

    @Override
    public void setProfilePicture(ImageRequest imageRequest) throws IOException {
        Optional<User> user = userDAO.findByUsername(imageRequest.getUser());
        if (user.isPresent()) {
            byte[] byteArr = imageRequest.getUploadImageData().getBytes();
            ProfilePicture profilePic = new ProfilePicture();
            profilePic.setName(imageRequest.getUploadImageData().getOriginalFilename());
            profilePic.setUser(user.get());
            profilePic.setEncodedPicture(compressBytes(byteArr));
            profilePicDAO.save(profilePic);
        }
    }

    // compress the image bytes before storing it in the database
    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }


    // uncompress the image bytes before returning it to the angular application
    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ioe) {
            ioe.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
