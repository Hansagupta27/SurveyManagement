package com.marketlogic.SurveyManagement.repository;

import com.marketlogic.SurveyManagement.dto.AnswerDto;
import com.marketlogic.SurveyManagement.dto.AnswerMappingQuestions;
import com.marketlogic.SurveyManagement.dto.SurveyAnswerDto;
import com.marketlogic.SurveyManagement.exception.CustomErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AnswerRepository {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private final String CHECK_SURVEY_EXIST_QUERY = "SELECT 1 FROM Survey WHERE surveyId = :surveyId";
    private final String CHECK_QUESTION_ORDER_EXIST_QUERY = "SELECT questionId FROM Question WHERE surveyId = :surveyId and questionOrder = :questionOrder";
    private final String INSERT_USER_QUERY = "insert into ResponseUser (userId, name, emailId) values (:userId,:name,:emailId)";
    private final String INSERT_RESPONSE_QUERY = "insert into Response (responseDate, surveyId, userId) values (:responseDate, :surveyId, :userId)";
    private final String INSERT_ANSWER_QUERY = "insert into Answer (answerText, questionId, ResponseId) values (:answerText, :questionId, :responseId)";

    private final String CHECK_ANSWER_TEXT_OPTION_QUERY = "SELECT questionOptionId FROM QuestionOptions WHERE questionId = :questionId and optionText = :optionText";

    private final String INSERT_ANSWER_OPTION_QUERY = "insert into AnswerOptions (questionOptionId, answerId) values (:questionOptionId, :answerId)";

    public boolean validateSurveyId(UUID surveyId) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("surveyId", surveyId);
            namedParameterJdbcTemplate.queryForObject(CHECK_SURVEY_EXIST_QUERY, parameters, Integer.class);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (DataAccessException e) {
            throw new CustomErrorException("Error while validating data in Survey dB." + e.getMessage());
        }
    }

    public Optional<UUID> validateQuestionOrder(int questionOrderNumber, UUID surveyId) {

        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("surveyId", surveyId)
                    .addValue("questionOrder", questionOrderNumber);

            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(CHECK_QUESTION_ORDER_EXIST_QUERY, parameters, UUID.class));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new CustomErrorException("Error while validating data in Question dB"+ e.getMessage());
        }

    }

    public Optional<UUID> validateAnswerText(UUID questionId, String answerText) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("questionId", questionId)
                    .addValue("optionText", answerText);

            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(CHECK_ANSWER_TEXT_OPTION_QUERY, parameters, UUID.class));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new CustomErrorException("Error while reading data from QuestionOption dB");
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAnswer(SurveyAnswerDto surveyAnswerDto, List<AnswerMappingQuestions> answerMappingQuestionsList) {
        saveUser(surveyAnswerDto);
        UUID responseId = addResponseForSurvey(surveyAnswerDto.getSurveyId(),surveyAnswerDto.getUserId());
        addAnswerForSurvey(responseId, answerMappingQuestionsList, surveyAnswerDto.getAnswers());
    }
    public void saveUser(SurveyAnswerDto surveyAnswerDto) {
        try{
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("userId", surveyAnswerDto.getUserId())
                    .addValue("name", surveyAnswerDto.getUserName())
                    .addValue("emailId", surveyAnswerDto.getEmailId());

            namedParameterJdbcTemplate.update(INSERT_USER_QUERY,parameters);
        }catch (DataAccessException e){
            throw new CustomErrorException("Error while loading data in ResponseUser dB." + e.getMessage());
        }
    }

    private UUID addResponseForSurvey(UUID surveyId, String userId) {
        try{
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("userId", userId)
                    .addValue("surveyId", surveyId)
                    .addValue("responseDate", LocalDateTime.now());

            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(INSERT_RESPONSE_QUERY, parameters, generatedKeyHolder, new String[]{"RESPONSEID"});
            return (UUID) generatedKeyHolder.getKeys().get("RESPONSEID");

        }catch (DataAccessException e){
            throw new CustomErrorException("Error while loading data in ResponseUser dB." + e.getMessage());
        }
    }

    private void addAnswerForSurvey(UUID responseId
            , List<AnswerMappingQuestions> answerMappingQuestionsList
            , List<AnswerDto> answerDtoList) {
        try{
            AtomicInteger val = new AtomicInteger(0);
            for(AnswerDto answerDto: answerDtoList){
                MapSqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue("responseId", responseId)
                        .addValue("answerText", answerDto.getAnswerText())
                        .addValue("questionId", answerMappingQuestionsList.get(val.get()).getQuestionId());

                GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(INSERT_ANSWER_QUERY, parameters, generatedKeyHolder, new String[]{"ANSWERID"});
                UUID answerId = (UUID) generatedKeyHolder.getKeys().get("ANSWERID");

                addAnswerOptionForSurvey(answerId,answerMappingQuestionsList.get(val.get()).getQuestionOptionId());

            }
        }catch (DataAccessException e){
            throw new CustomErrorException("Error while loading data in ResponseUser dB." + e.getMessage());
        }
    }



    private void addAnswerOptionForSurvey(UUID answerId, UUID questionOptionId) {
        try{
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("answerId", answerId)
                    .addValue("questionOptionId", questionOptionId);

            namedParameterJdbcTemplate.update(INSERT_ANSWER_OPTION_QUERY, parameters);

        }catch (DataAccessException e){
            throw new CustomErrorException("Error while loading data in AnswerOption dB." + e.getMessage());
        }
    }
}
