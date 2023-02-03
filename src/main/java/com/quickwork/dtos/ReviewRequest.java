package com.quickwork.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    private Long id;
    private String title;
    private String content;
    private int rating;
    private String reviewerUsername;
    private String reviewedUsername;
}
