package com.marketlogic.SurveyManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Validated
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDto {

    @NotBlank(message = "Name is mandatory")
    public String surveyName;
    @Valid
    @NotEmpty
    public List<QuestionDto> questions;

}
