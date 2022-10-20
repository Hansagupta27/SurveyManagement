package com.marketlogic.SurveyManagement.controller;

import com.marketlogic.SurveyManagement.dto.SurveyAnswerDto;
import com.marketlogic.SurveyManagement.dto.SurveyCreatedResponse;
import com.marketlogic.SurveyManagement.dto.SurveyDto;
import com.marketlogic.SurveyManagement.service.AnswerService;
import com.marketlogic.SurveyManagement.exception.CustomValidationErrorException;
import com.marketlogic.SurveyManagement.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private AnswerService answerService;

    @PostMapping("/create")
    public SurveyCreatedResponse createSurvey(@Valid @RequestBody final SurveyDto survey) throws CustomValidationErrorException {
        return surveyService.createSurveyWithMCQ(survey);
    }

    @PostMapping("/submit")
    public ResponseEntity submitSurvey(@Valid @RequestBody final SurveyAnswerDto surveyAnswerDto)
            throws CustomValidationErrorException {
        answerService.submitSurveyWithMCQ(surveyAnswerDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
