package com.quickwork.service;

import com.quickwork.model.RegistrationRequest;
import com.quickwork.model.User;
import com.quickwork.repository.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegistrationService(UserDAO userDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDAO = userDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String register(RegistrationRequest request) throws IllegalStateException {
        if (userDAO.findByEmail(request.getEmail()).isPresent() || userDAO.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("User with that username or email already exists!");
        }
        String encodedPass = bCryptPasswordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), request.getEmail(), encodedPass, request.getRoleCode(), request.getPhoneNumber());
        userDAO.save(user);
        return "User " + request.getUsername() + " successfully registered";
    }


}
