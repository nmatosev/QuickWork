package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {

    String messageContent;
    String user1;
    String user2;
    String weekDay;
    long adId;
}
