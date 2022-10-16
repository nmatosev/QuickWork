package com.quickwork.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long id;
    private String title;
    private String content;
    private int rating;
    private String reviewerUsername;
    private String reviewedUsername;
}
