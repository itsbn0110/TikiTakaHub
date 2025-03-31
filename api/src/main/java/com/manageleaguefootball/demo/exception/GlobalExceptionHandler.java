package com.manageleaguefootball.demo.exception;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
        
        @ExceptionHandler(value = Exception.class)
        ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception) {

        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_USER;

        return ResponseEntity.badRequest()
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
        }

        @ExceptionHandler(value = AppException.class)
        ResponseEntity<ApiResponse> handlingRuntimeException(AppException exception) {

                return ResponseEntity.status(exception.getErrorCode().getStatusCode())
                        .body(ApiResponse.builder()
                                .code(exception.getErrorCode().getCode())
                                .message(exception.getErrorCode().getMessage())
                                .build());
        }

}
