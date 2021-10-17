package com.quickwork.model;

import lombok.Data;

@Data
public class RegistrationRequest {

    private final String username;
    private final String password;
    private final String email;
    private final String roleCode;

}
