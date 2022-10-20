package com.marketlogic.SurveyManagement.dto;

import com.marketlogic.SurveyManagement.utility.UUIDValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Getter
@Validated
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAnswerDto {

    @UUIDValidation
    public String surveyId;
    @NotBlank(message = "UserID is mandatory")
    public String userId;
    @NotBlank(message = "Name is mandatory")
    public String userName;
    @Email(message = "Invalid email")
    public String emailId;

    @Valid
    @NotEmpty
    public List<AnswerDto> answers;

    public UUID getSurveyId() {
        return UUID.fromString(surveyId);
    }

}
