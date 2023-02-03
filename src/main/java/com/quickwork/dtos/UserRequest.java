package com.quickwork.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private long id;
    private String username;
    private String rating;
    private String token;
    private List<String> roles = new ArrayList<>();
    private String email;
    private String phoneNumber;
}
