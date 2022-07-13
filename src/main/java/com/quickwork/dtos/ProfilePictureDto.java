package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePictureDto {

    private long id;
    private String name;
    private String type;
    private String encodedPicture;
}
