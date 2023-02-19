package com.quickwork.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
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
