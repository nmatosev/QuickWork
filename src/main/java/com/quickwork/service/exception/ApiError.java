package com.quickwork.service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@AllArgsConstructor
@Data
@Builder
@RequiredArgsConstructor
@Slf4j
public class ApiError {

    @NotNull
    @Valid
    private OffsetDateTime timestamp = OffsetDateTime.now();

    private String message;
    private String details;
}
