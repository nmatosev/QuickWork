package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdDto {

    private long id;
    private String title;
    private String content;
    private long countyId;
    private long userId;
    private String validUntil;
    private String county;
    private UserDto user;

}
