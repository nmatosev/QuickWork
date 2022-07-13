package com.quickwork.dtos;

import com.quickwork.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
