package com.marketlogic.SurveyManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Validated
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto {

    @Min(value = 1, message = "Question order should not be less than 1")
    @Max(value = 50, message = "Question order should not be greater than 50")
    public int questionOrderNumber;

    @Size(min = 2, max = 200, message
            = "Answer Text must be between 2 and 200 characters")
    @NotBlank(message = "Answer Text is mandatory")
    public String answerText;

}
