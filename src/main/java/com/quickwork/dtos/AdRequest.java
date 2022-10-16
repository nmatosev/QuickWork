package com.quickwork.dtos;

import lombok.Data;

@Data
public class AdRequest {
    private long id;
    private String title;
    private String content;
    private long countyId;
    private long userId;
    private String validUntil;
    private String county;
    private UserResponse user;
}
