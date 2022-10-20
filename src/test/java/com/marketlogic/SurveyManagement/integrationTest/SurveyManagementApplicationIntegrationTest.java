package com.marketlogic.SurveyManagement.integrationTest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "pretty", features = "src/test/resources/features", glue =
        "com.marketlogic.SurveyManagement.integrationTest")
public class SurveyManagementApplicationIntegrationTest {


}
