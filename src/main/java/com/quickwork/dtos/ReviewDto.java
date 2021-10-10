package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

    private Long id;
    private String title;
    private String content;
    private int rating;
}
