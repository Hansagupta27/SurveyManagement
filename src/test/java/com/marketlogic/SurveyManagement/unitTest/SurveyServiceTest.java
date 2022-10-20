package com.marketlogic.SurveyManagement.unitTest;

import com.marketlogic.SurveyManagement.dto.QuestionDto;
import com.marketlogic.SurveyManagement.dto.QuestionOptionsDto;
import com.marketlogic.SurveyManagement.dto.SurveyDto;
import com.marketlogic.SurveyManagement.exception.CustomValidationErrorException;
import com.marketlogic.SurveyManagement.repository.SurveyRepository;
import com.marketlogic.SurveyManagement.service.SurveyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SurveyServiceTest {
    @Mock
    SurveyRepository surveyRepository;

    @InjectMocks
    SurveyService surveyService;

    @Test
    public void succesfullyValidateCreateSurvey() throws CustomValidationErrorException {
        SurveyDto surveyDto = getInputSurveyDto();
        surveyService.createSurveyWithMCQ(surveyDto);
        Mockito.verify(surveyRepository).createSurvey(surveyDto);

    }

    @Test
    public void throwValidationExceptionWhenInputNotValid() {
        List<QuestionDto> questionDtoLists = new ArrayList();
        List<QuestionOptionsDto> questionOptionsDtoList = new ArrayList<>();
        questionOptionsDtoList.add(QuestionOptionsDto.builder().optionOrder(1).optionText("This is an option")
                .build());
        questionDtoLists.add(QuestionDto.builder()
                        .questionOrderNumber(1)
                        .questionText("This is first question")
                        .questionOptions(questionOptionsDtoList).build());

        questionDtoLists.add(QuestionDto.builder()
                .questionOrderNumber(1)
                .questionText("This is second question")
                .questionOptions(questionOptionsDtoList).build());

        SurveyDto surveyDto = SurveyDto.builder().surveyName("testing survey")
                .questions(questionDtoLists)
                .build();

        CustomValidationErrorException exception = Assertions.assertThrows(CustomValidationErrorException.class, () -> {
            surveyService.createSurveyWithMCQ(surveyDto);
        });
        Assertions.assertTrue("Question Order number is not unique".equalsIgnoreCase(exception.getMessage()));
    }

    private SurveyDto getInputSurveyDto() {
        List<QuestionDto> questionDtoLists = new ArrayList();
        List<QuestionOptionsDto> questionOptionsDtoList = new ArrayList<>();
        questionOptionsDtoList.add(QuestionOptionsDto.builder().optionOrder(1).optionText("This is an option")
                .build());
        questionDtoLists.add(QuestionDto.builder()
                .questionOrderNumber(1)
                .questionText("This is first question")
                .questionOptions(questionOptionsDtoList)
                .build());

        return SurveyDto.builder()
                .surveyName("Test Survey")
                .questions(questionDtoLists)
                .build();

    }

}
