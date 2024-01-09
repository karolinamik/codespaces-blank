package ch.skycell;

import ch.skycell.helpers.FileReader;
import ch.skycell.helpers.LoggerRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggerDataStepDefinitions extends LoggerRequest {
    private String loggerNumber;
    private Response dataResponse;

    @Given("the logger with type {string} is created")
    public void the_logger_with_type_is_created(String string) {
        loggerNumber = generateLoggerNumber();
        Response sensorResponse = createLogger(string, loggerNumber);

        assertEquals(201, sensorResponse.getStatusCode());
    }

    @When("data is sent for created logger")
    public void data_is_sent_for_created_logger() {
        String content = FileReader.readResourceFile( "requests/logger_data_request.json");
        content = content.replaceAll("LOGGER_NUMBER", loggerNumber);
        JSONObject requestBody = new JSONObject(content);

        dataResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("APIKEY", System.getenv("SKYCELL_API_KEY"))
                .body(requestBody)
                .when()
                .post("/v1/lora/uplink");
    }

    @Then("the system should save it")
    public void the_system_should_save_it() {
        assertEquals(201, dataResponse.getStatusCode());
    }
}
