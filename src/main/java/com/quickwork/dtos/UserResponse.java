package com.quickwork.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse {
    private long id;
    private String username;
    private List<AdResponse> ads;
    private List<ReviewResponse> reviews;
    private String rating;
    private String token;
    private List<String> roles = new ArrayList<>();
    private String email;
    private String phoneNumber;
}
