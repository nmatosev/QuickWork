package com.quickwork.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class AdChatResponse {

    private long adId;
    private String title;
    private String content;
    List<MessageResponse> messages = new ArrayList<>();
}
