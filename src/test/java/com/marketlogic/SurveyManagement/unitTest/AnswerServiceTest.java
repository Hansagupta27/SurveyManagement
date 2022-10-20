package com.marketlogic.SurveyManagement.unitTest.service;

import com.marketlogic.SurveyManagement.dto.AnswerDto;
import com.marketlogic.SurveyManagement.dto.SurveyAnswerDto;
import com.marketlogic.SurveyManagement.exception.CustomValidationErrorException;
import com.marketlogic.SurveyManagement.repository.AnswerRepository;
import com.marketlogic.SurveyManagement.service.AnswerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {
    @Mock
    AnswerRepository answerRepository;

    @InjectMocks
    AnswerService answerService;

    @Test
    public void successfullySubmitSurvey() throws CustomValidationErrorException {
        Mockito.when(answerRepository.validateSurveyId(Mockito.any())).thenReturn(true);
        Optional<UUID> questionId = Optional.of(UUID.fromString("613ec90e-8aa7-49f6-9f53-7a5965eade06"));
        Mockito.doReturn(questionId).when(answerRepository).validateQuestionOrder(Mockito.anyInt(),Mockito.any());

        Mockito.when(answerRepository.validateAnswerText(Mockito.any(),Mockito.anyString()))
                .thenReturn(Optional.of(UUID.fromString("613ec90e-8aa7-49f6-9f53-7a5965eade06")));

        answerService.submitSurveyWithMCQ(getInputSurveyAnswerDto());
        Mockito.verify(answerRepository).saveAnswer(Mockito.any(),Mockito.anyList());

    }

    @Test
    public void throwExceptionWhenSurveyIdNotPresentInDb() {

        Mockito.when(answerRepository.validateSurveyId(Mockito.any())).thenReturn(false);
        CustomValidationErrorException exception = Assertions.assertThrows(CustomValidationErrorException.class, () -> {
            answerService.submitSurveyWithMCQ(getInputSurveyAnswerDto());
        });

        Assertions.assertTrue("Survey Id not present in DB".equalsIgnoreCase(exception.getMessage()));
    }

    @Test
    public void throwExceptionWhenQuestionIdIsNotMappedInSurvey() {

        Mockito.when(answerRepository.validateSurveyId(Mockito.any())).thenReturn(true);
        Mockito.doReturn(Optional.empty()).when(answerRepository).validateQuestionOrder(Mockito.anyInt(),Mockito.any());
        CustomValidationErrorException exception = Assertions.assertThrows(CustomValidationErrorException.class, () -> {
            answerService.submitSurveyWithMCQ(getInputSurveyAnswerDto());
        });

        Assertions.assertTrue("Invalid Question order number for the given surveyId".equalsIgnoreCase(exception.getMessage()));
    }

    @Test
    public void throwExceptionWhenQuestionOptionIdIsNotMappedInQuestion() {

        Mockito.when(answerRepository.validateSurveyId(Mockito.any())).thenReturn(true);
        Optional<UUID> questionId = Optional.of(UUID.fromString("613ec90e-8aa7-49f6-9f53-7a5965eade06"));
        Mockito.doReturn(questionId).when(answerRepository).validateQuestionOrder(Mockito.anyInt(),Mockito.any());

        Mockito.when(answerRepository.validateAnswerText(Mockito.any(),Mockito.anyString())).thenReturn(Optional.empty());


        CustomValidationErrorException exception = Assertions.assertThrows(CustomValidationErrorException.class, () -> {
            answerService.submitSurveyWithMCQ(getInputSurveyAnswerDto());
        });

        Assertions.assertTrue("Invalid Answer Text for given Question order number in surveyId"
                .equalsIgnoreCase(exception.getMessage()));
    }

    @Test
    public void throwExceptionWhenDoubleOrderNumberIsPresentInInoutRequest() {

        Mockito.when(answerRepository.validateSurveyId(Mockito.any())).thenReturn(true);
        Optional<UUID> questionId = Optional.of(UUID.fromString("613ec90e-8aa7-49f6-9f53-7a5965eade06"));
        Mockito.doReturn(questionId).when(answerRepository).validateQuestionOrder(Mockito.anyInt(),Mockito.any());

        Mockito.when(answerRepository.validateAnswerText(Mockito.any(),Mockito.anyString()))
                .thenReturn(Optional.of(UUID.fromString("613ec90e-8aa7-49f6-9f53-7a5965eade06")));


        CustomValidationErrorException exception = Assertions.assertThrows(CustomValidationErrorException.class, () -> {
            answerService.submitSurveyWithMCQ(getInputSurvetAnswerDtoWithDoubleQuestionOrderNumber());
        });

        Assertions.assertTrue("Duplicate Question order number is present in request with value 1"
                .equalsIgnoreCase(exception.getMessage()));
    }

    private SurveyAnswerDto getInputSurveyAnswerDto() {

        List<AnswerDto> answerDtoLists = new ArrayList();
        answerDtoLists.add(AnswerDto.builder().questionOrderNumber(1)
                .answerText("India").build());

        return SurveyAnswerDto.builder().surveyId("613ec90e-8aa7-49f6-9f53-7a5965eade06")
                .userId("TTest")
                .userName("Test")
                .emailId("test@test.com")
                .answers(answerDtoLists)
                .build();
    }

    private SurveyAnswerDto getInputSurvetAnswerDtoWithDoubleQuestionOrderNumber() {

        List<AnswerDto> answerDtoLists = new ArrayList();
        answerDtoLists.add(AnswerDto.builder().questionOrderNumber(1)
                .answerText("India").build());
        answerDtoLists.add(AnswerDto.builder().questionOrderNumber(1)
                .answerText("India").build());

        return SurveyAnswerDto.builder().surveyId("613ec90e-8aa7-49f6-9f53-7a5965eade06")
                .userId("TTest")
                .userName("Test")
                .emailId("test@test.com")
                .answers(answerDtoLists)
                .build();

    }

}
