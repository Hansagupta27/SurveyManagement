package com.marketlogic.SurveyManagement.service;

import com.marketlogic.SurveyManagement.dto.AnswerDto;
import com.marketlogic.SurveyManagement.dto.AnswerMappingQuestions;
import com.marketlogic.SurveyManagement.dto.SurveyAnswerDto;
import com.marketlogic.SurveyManagement.exception.CustomValidationErrorException;
import com.marketlogic.SurveyManagement.repository.AnswerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AnswerService {

    @Autowired
    AnswerRepository answerRepository;
    public void submitSurveyWithMCQ(SurveyAnswerDto surveyAnswerDto) throws CustomValidationErrorException {

        List<AnswerMappingQuestions> mappingQuestionsList = validateIncomingRequest(surveyAnswerDto);
        answerRepository.saveAnswer(surveyAnswerDto,mappingQuestionsList);
        log.info("Answers for Survey has been successfully saved in database");
    }

    private List<AnswerMappingQuestions> validateIncomingRequest(SurveyAnswerDto surveyAnswerDto) throws CustomValidationErrorException {

        if(!answerRepository.validateSurveyId(surveyAnswerDto.getSurveyId())){
            throw new CustomValidationErrorException("Survey Id not present in DB");
        }

        List<AnswerMappingQuestions> mappingQuestions = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        for(AnswerDto answers: surveyAnswerDto.getAnswers()){
            Optional<UUID> questionId = answerRepository.validateQuestionOrder(answers.getQuestionOrderNumber()
                    , surveyAnswerDto.getSurveyId());
            if(questionId.isEmpty())
                throw new CustomValidationErrorException("Invalid Question order number for the given surveyId");

            Optional<UUID> questionOptionId = answerRepository.validateAnswerText(questionId.get(), answers.getAnswerText());
            if(questionOptionId.isEmpty())
                throw new CustomValidationErrorException("Invalid Answer Text for given Question order number in surveyId");

            mappingQuestions = checkIfAlreadyAddedToSetAndUpdateList(mappingQuestions, set, answers, questionId, questionOptionId);
        }
        return mappingQuestions;
    }

    private List<AnswerMappingQuestions> getMappedList(
            final List<AnswerMappingQuestions> mappingQuestions,
            final HashSet<Integer> questionOrderSet,
            final AnswerDto answer,
            final Optional<UUID> questionId,
            final Optional<UUID> questionOptionId ) {

        mappingQuestions.add(AnswerMappingQuestions.builder()
                .questionOrder(answer.getQuestionOrderNumber())
                .questionId(questionId.get())
                .questionOptionId(questionOptionId.get())
                .build());
        questionOrderSet.add(answer.getQuestionOrderNumber());

        return mappingQuestions;
    }

    private List<AnswerMappingQuestions> checkIfAlreadyAddedToSetAndUpdateList(
            List<AnswerMappingQuestions> mappingQuestions,
            final HashSet<Integer> questionOrderSet,
            final AnswerDto answer,
            final Optional<UUID> questionId,
            final Optional<UUID> questionOptionId ) throws CustomValidationErrorException {
        if( !questionOrderSet.contains( answer.getQuestionOrderNumber() ) ) {
            return getMappedList( mappingQuestions, questionOrderSet, answer, questionId, questionOptionId );
        }
        throw new CustomValidationErrorException("Duplicate Question order number is present in request " +
                "with value "+ answer.getQuestionOrderNumber());
    }
}
