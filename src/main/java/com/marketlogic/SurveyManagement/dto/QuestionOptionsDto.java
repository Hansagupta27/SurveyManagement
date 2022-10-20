package com.marketlogic.SurveyManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Validated
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionOptionsDto {

    @Min(value = 1, message = "Options should not be less than 1")
    @Max(value = 5, message = "Options should not be greater than 5")
    public int optionOrder;

    @NotBlank(message = "Question Option Text is mandatory")
    public String optionText;
}
