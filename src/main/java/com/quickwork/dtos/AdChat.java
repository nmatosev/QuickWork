package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdChat {

    private long adId;
    private String title;
    private String content;
    List<MessageDto> messages = new ArrayList<>();
}
