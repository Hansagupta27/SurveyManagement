package com.marketlogic.SurveyManagement.controller;

import com.marketlogic.SurveyManagement.dto.ErrorResponse;
import com.marketlogic.SurveyManagement.exception.CustomValidationErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
class CustomExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(errorMessage)
                        .status(status.name()).build(), status);
    }

    @ExceptionHandler(CustomValidationErrorException.class)
    public ResponseEntity<ErrorResponse> handleCustomValidationError(CustomValidationErrorException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(e.getMessage())
                        .status(status.name()).build(), status);
    }

}
