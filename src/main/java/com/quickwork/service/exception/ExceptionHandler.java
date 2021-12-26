package com.quickwork.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {


    @org.springframework.web.bind.annotation.ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleBadRequest(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("Error", methodArgumentNotValidException);

        String details = Objects.requireNonNull(methodArgumentNotValidException.getBindingResult().getFieldError()).getDefaultMessage();
        ApiError apiError = ApiError.builder().timestamp(OffsetDateTime.now()).message(methodArgumentNotValidException.getMessage()).details(details)
                .build();
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(apiError, responseStatus);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({Throwable.class})
    public ResponseEntity<Object> handleEverythingElse(Throwable e) {
        ApiError responseMessage = ApiError.builder().timestamp(OffsetDateTime.now()).message(e.getMessage()).build();
        //esponseMessage.setDetails(Arrays.asList(e.getStackTrace()).toString());
        responseMessage.setMessage(e.getMessage());
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(responseMessage, responseStatus);
    }
}
