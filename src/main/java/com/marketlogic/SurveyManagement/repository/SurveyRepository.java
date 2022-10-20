package com.marketlogic.SurveyManagement.repository;

import com.marketlogic.SurveyManagement.dto.QuestionDto;
import com.marketlogic.SurveyManagement.dto.QuestionOptionsDto;
import com.marketlogic.SurveyManagement.dto.SurveyDto;
import com.marketlogic.SurveyManagement.exception.CustomErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SurveyRepository {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private final String INSERT_QUERY = "insert into Survey (surveyName, createdOn,modifiedOn ) values (:surveyName,:createdOn,:modifiedOn)";
    private final String INSERT_QUESTION_QUERY = "insert into Question (questionOrder, questionText,surveyId ) values (:questionOrder,:questionText,:surveyId)";

    private final String INSERT_QUESTION_OPTION_QUERY = "insert into QuestionOptions (optionOrder, optionText, questionId ) values (:optionOrder,:optionText,:questionId)";

    @Transactional
    public UUID createSurvey(final SurveyDto surveyDto) {
        try{
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("surveyName", surveyDto.getSurveyName())
                    .addValue("createdOn", LocalDateTime.now())
                    .addValue("modifiedOn", LocalDateTime.now());
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(INSERT_QUERY,parameters,generatedKeyHolder, new String[]{"SURVEYID"});
            UUID surveyID = (UUID) generatedKeyHolder.getKeys().get("SURVEYID");
            addQuestionsInSurvey(surveyDto.getQuestions(),surveyID);
            return surveyID;
        }catch (DataAccessException e){
            throw new CustomErrorException("Error while loading data in Survey dB." + e.getMessage());
        }
    }

    public void addQuestionsInSurvey(final List<QuestionDto> questionDtoList, final UUID surveyId) {
        try {
            for (QuestionDto questions : questionDtoList) {
                MapSqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue("questionOrder", questions.getQuestionOrderNumber())
                        .addValue("questionText", questions.getQuestionText())
                        .addValue("surveyId", surveyId);
                GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(INSERT_QUESTION_QUERY, parameters, generatedKeyHolder, new String[]{"QUESTIONID"});
                UUID questionId = (UUID) generatedKeyHolder.getKeys().get("QUESTIONID");
                addQuestionOptions(questions.getQuestionOptions(), questionId);
            }
        }catch (DataAccessException e){
            throw new CustomErrorException("Error while loading data in Question dB." + e.getMessage());
        }

    }

    public void addQuestionOptions(final List<QuestionOptionsDto> questionDtoList, final UUID questionId) {
        try {
            List<MapSqlParameterSource> params = new ArrayList<>();
            for (QuestionOptionsDto questionOptions : questionDtoList) {
                MapSqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue("optionOrder", questionOptions.getOptionOrder())
                        .addValue("optionText", questionOptions.getOptionText())
                        .addValue("questionId", questionId);
                params.add(parameters);
            }
            namedParameterJdbcTemplate.batchUpdate(INSERT_QUESTION_OPTION_QUERY, params.toArray(MapSqlParameterSource[]::new));
        }catch (DataAccessException e){
            throw new CustomErrorException("Error while loading data in Question Option dB." + e.getMessage());
        }
    }


}
