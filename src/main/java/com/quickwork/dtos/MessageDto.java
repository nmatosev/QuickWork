package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {

    String messageContent;
    String sender;
    //AdDto adDto;
    long adId;
}
