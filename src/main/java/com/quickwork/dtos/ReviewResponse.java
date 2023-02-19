package com.quickwork.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private String title;
    private String content;
    private int rating;
    private String reviewerUsername;
    private String reviewedUsername;
}
