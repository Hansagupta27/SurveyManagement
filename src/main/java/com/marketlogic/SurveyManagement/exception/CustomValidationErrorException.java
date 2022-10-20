package com.marketlogic.SurveyManagement.exception;

import lombok.Getter;

@Getter
public class CustomValidationErrorException extends Throwable {

    public String message;
    public CustomValidationErrorException(String message) {
        this.message = message;
    }
}
