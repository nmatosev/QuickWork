package com.quickwork.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageResponse {

    String messageContent;
    String user1;
    String user2;
    String weekDay;
    Long adId;
}
