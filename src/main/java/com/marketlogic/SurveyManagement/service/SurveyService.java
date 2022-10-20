package com.marketlogic.SurveyManagement.service;

import com.marketlogic.SurveyManagement.dto.QuestionDto;
import com.marketlogic.SurveyManagement.dto.SurveyCreatedResponse;
import com.marketlogic.SurveyManagement.dto.SurveyDto;
import com.marketlogic.SurveyManagement.exception.CustomValidationErrorException;
import com.marketlogic.SurveyManagement.repository.SurveyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@Slf4j
public class SurveyService {

    @Autowired
    SurveyRepository surveyRepository;

    public SurveyCreatedResponse createSurveyWithMCQ(final SurveyDto surveyDto) throws CustomValidationErrorException {
        validateQuestionOrder(surveyDto.getQuestions());
        SurveyCreatedResponse responseDto = new SurveyCreatedResponse();
        responseDto.setSurveyId(surveyRepository.createSurvey(surveyDto));
        log.info("Survey created successfully in database");
        return responseDto;
    }

    private void validateQuestionOrder(List<QuestionDto> questionDtoList) throws CustomValidationErrorException {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        for(QuestionDto questions: questionDtoList){
            if(!set.add(questions.getQuestionOrderNumber()))
                throw new CustomValidationErrorException("Question Order number is not unique");
        }
    }


}
