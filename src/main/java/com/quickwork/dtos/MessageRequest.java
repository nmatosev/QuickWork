package com.quickwork.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageRequest {

    String messageContent;
    String sender;
    String receiver;
    long adId;
}
