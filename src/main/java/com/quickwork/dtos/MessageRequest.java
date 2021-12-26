package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

    String messageContent;
    String sender;
    long adId;
}
