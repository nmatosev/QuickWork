package com.quickwork.dtos;

import lombok.Data;

@Data
public class ReviewRequest {

    private Long id;
    private String title;
    private String content;
    private int rating;
    private String reviewerUsername;
    private String reviewedUsername;
}
