package ch.skycell;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;

public class LoggerCreationStepDefinitions {
    private final String SENSOR_BASE_URL = "https://sensor-data-ingestion.dev.skycell.ch";
    private Response sensorResponse;

    public Response request_to_create_logger(String loggerType, String loggerNumber) {
        if (loggerNumber == null) {
            loggerNumber = RandomStringUtils.random(16, "0123456789ABCDEF");
        }
        JSONObject jsonObj = new JSONObject()
                .put("loggerNumber", loggerNumber)
                .put("loggerType", loggerType)
                .put("baseInterval", 600);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("APIKEY", System.getenv("SKYCELL_API_KEY"))
                .body(jsonObj.toString())
                .when()
                .post("/v1/lora/configuration");
    }

    @Given("the API for logger creation is defined")
    public void the_api_for_logger_creation_is_defined() {
        RestAssured.baseURI = SENSOR_BASE_URL;
        String apiKey = System.getenv("SKYCELL_API_KEY");

        assertNotNull(apiKey);
        assert (!apiKey.isEmpty());
    }

    @When("a user makes API requests to create logger with type {string}")
    public void a_user_makes_api_requests_to_create_logger_with_type(String loggerType) {
        sensorResponse = request_to_create_logger(loggerType, null);
    }

    @Then("the system should respond with successful creation messages for each logger")
    public void the_system_should_respond_with_successful_creation_messages_for_each_logger() {
        assertEquals(201, sensorResponse.getStatusCode());
    }

    @Given("{string} or {string} is invalid")
    public void logger_type_or_logger_number_is_invalid(String loggerType, String loggerNumber) {
        sensorResponse = request_to_create_logger(loggerType, loggerNumber);
    }

    @Then("the system should not create the logger")
    public void the_system_should_not_create_the_logger() {
        // Write code here that turns the phrase above into concrete actions
        assertEquals(400, sensorResponse.getStatusCode());
    }
}
