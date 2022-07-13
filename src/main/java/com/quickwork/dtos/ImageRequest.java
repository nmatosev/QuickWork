package com.quickwork.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageRequest {

    private MultipartFile uploadImageData;
    private String user;
}
