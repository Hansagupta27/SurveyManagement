package com.marketlogic.SurveyManagement.integrationTest;

import com.marketlogic.SurveyManagement.dto.ErrorResponse;
import com.marketlogic.SurveyManagement.dto.SurveyAnswerDto;
import com.marketlogic.SurveyManagement.dto.SurveyCreatedResponse;
import com.marketlogic.SurveyManagement.dto.SurveyDto;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.UUID;

public class SurveyStepDefinition {

    ResponseEntity createResponse;
    ResponseEntity submitResponse;
    @Autowired
    public SurveyClient surveyClient;

    @When("the client sends input post call")
    public void the_client_sends_input_post_call() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SurveyDto createSurveyInput = objectMapper.readValue(new ClassPathResource("CreateSurveyInput.json")
                .getFile(), SurveyDto.class);
        createResponse = surveyClient.postCreateSurvey(createSurveyInput, SurveyCreatedResponse.class);


    }
    @Then("the client receives response")
    public void the_client_receives_response() {

        Assertions.assertThat(createResponse.getBody()).isInstanceOf(SurveyCreatedResponse.class);
    }
    @Then("the client receives information about the surveyID with {int}")
    public void the_client_receives_information_about_the_survey_id_with(int responseCode) {
        Assertions.assertThat(createResponse.getStatusCodeValue()).isEqualTo(responseCode);
    }

    @When("the client sends input post call with invalid data")
    public void theClientSendsInputPostCallWithInvalidData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SurveyDto createSurveyInput = objectMapper.readValue(new ClassPathResource("InvalidCreateSurveyInput.json")
                .getFile(), SurveyDto.class);
        createResponse = surveyClient.postCreateSurvey(createSurveyInput, ErrorResponse.class);
    }

    @Then("the client receives error response")
    public void theClientReceivesErrorResponse() {
        Assertions.assertThat(createResponse.getBody()).isInstanceOf(String.class);


    }

    @And("the client receives information about the error details {string}")
    public void theClientReceivesInformationAboutTheErrorDetails(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse;
        try {
            errorResponse = objectMapper.readValue((String)createResponse.getBody(), ErrorResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThat(errorResponse.getStatus()).isEqualTo("BAD_REQUEST");
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo(message);
    }

    @When("the client sends input post call with valid data")
    public void the_client_sends_input_post_call_with_valid_data() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SurveyAnswerDto submitSurveyDto = objectMapper.readValue(new ClassPathResource("SubmitSurveyInput.json")
                .getFile(), SurveyAnswerDto.class);
        the_client_sends_input_post_call();
        SurveyCreatedResponse surveyCreatedResponse = (SurveyCreatedResponse) createResponse.getBody();

        SurveyAnswerDto updatedSurveyAnswerDto = SurveyAnswerDto.builder()
                .surveyId(surveyCreatedResponse.getSurveyId().toString())
                .answers(submitSurveyDto.getAnswers())
                .emailId(submitSurveyDto.getEmailId())
                .userId(submitSurveyDto.getUserId())
                .userName(submitSurveyDto.getUserName())
                .build();

        submitResponse = surveyClient.postSubmitSurvey(updatedSurveyAnswerDto);
    }

    @When("the client receives information about the successful submission details with  {int}")
    public void the_client_receives_information_about_the_successful_submission_details_with(Integer statusCode) {
        Assertions.assertThat(submitResponse.getStatusCodeValue()).isEqualTo(statusCode);
    }
}