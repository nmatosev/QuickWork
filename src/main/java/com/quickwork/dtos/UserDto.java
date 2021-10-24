package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {
    private long id;
    private String username;
    private List<AdDto> ads;
    private List<ReviewDto> reviews;
    private String rating;
    private String token;
    private List<String> roles = new ArrayList<>();
    private String email;


}
