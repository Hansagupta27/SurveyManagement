package com.marketlogic.SurveyManagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class CustomErrorException extends RuntimeException {
    private HttpStatus status;
    private String message;
    public CustomErrorException(HttpStatus status, String message) {
        this.message=message;
        this.status = status;
    }
    public CustomErrorException(String message) {
        this.message=message;

    }

    public CustomErrorException() {

    }
}
