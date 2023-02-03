package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdResponse {

    private long id;
    private String title;
    private String content;
    private long countyId;
    private long userId;
    private String validUntil;
    private String county;
    private UserResponse user;

}
