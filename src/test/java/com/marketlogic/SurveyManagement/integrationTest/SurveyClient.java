package com.marketlogic.SurveyManagement.integrationTest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.marketlogic.SurveyManagement.dto.SurveyAnswerDto;
import com.marketlogic.SurveyManagement.dto.SurveyCreatedResponse;
import com.marketlogic.SurveyManagement.dto.SurveyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class SurveyClient {

    private final static String BASE_URI = "http://localhost";
    private final static String CREATE_SURVEY_ENDPOINT = "/survey/create";
    private final static String SUBMIT_SURVEY_ENDPOINT = "/survey/submit";

    @LocalServerPort
    private int port;

    WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options()
            .port(WireMockConfiguration.wireMockConfig().dynamicPort().portNumber()));

    @Autowired
    private RestTemplate restTemplate;

    private URI createSurveyEndpoint() {
        return URI.create(BASE_URI + ":" + port + CREATE_SURVEY_ENDPOINT);
    }

    private URI submitSurveyEndpoint() {
        return URI.create(BASE_URI + ":" + port + SUBMIT_SURVEY_ENDPOINT);
    }


    public ResponseEntity postCreateSurvey(SurveyDto surveyDto, Class responseObjectType){
        wireMockServer.start();

        try{
            return restTemplate.postForEntity(createSurveyEndpoint(), surveyDto, responseObjectType );
        } catch(HttpClientErrorException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> postSubmitSurvey(SurveyAnswerDto surveyAnswerDto){

        try{
            return restTemplate.postForEntity(submitSurveyEndpoint(), surveyAnswerDto, String.class );
        } catch(HttpClientErrorException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}
