package com.marketlogic.SurveyManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerMappingQuestions {

    public int questionOrder;
    public UUID questionId;
    public UUID questionOptionId;

}
