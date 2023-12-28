package ch.skycell;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {
    private final String AUTH_BASE_URL = "https://keycloak.dev.skycell.ch";
    private final String SENSOR_BASE_URL = "https://sensor-data-ingestion.dev.skycell.ch";
    private Response authResponse;
    private String accessToken;
    private Response sensorResponse;

    @Given("credentials are definied")
    public void credentials_are_definied() {
        String username = System.getenv("SKYCELL_USERNAME");
        String password = System.getenv("SKYCELL_PASSWORD");

        assertNotNull(username);
        assertNotNull(password);
        assert (username.length() > 0);
        assert (password.length() > 0);

    }

    @When("sending a request to authenticate")
    public void sending_a_request_to_authenticate() {
        RestAssured.baseURI = AUTH_BASE_URL;

        authResponse = RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam("client_id", "webapp")
                .formParam("grant_type", "password")
                .formParam("username", System.getenv("SKYCELL_USERNAME"))
                .formParam("password", System.getenv("SKYCELL_PASSWORD"))
                .when()
                .post("/realms/skycell/protocol/openid-connect/token");

        assertEquals(authResponse.getStatusCode(), 200);
    }

    @Then("the response contains authentication token")
    public void the_response_contains_authentication_token() {
        JsonPath jsonPath = new JsonPath(authResponse.asString());
        accessToken = jsonPath.getString("access_token");

        assertNotNull(accessToken);
        assert (accessToken.length() > 0);
    }

    @Then("the token is a valid JWT token")
    public void the_token_is_a_valid_jwt_token() {
        String issuer = "https://keycloak.dev.skycell.ch/realms/skycell";
        Instant now = Instant.now();
        DecodedJWT jwt = JWT.decode(accessToken);

        assertTrue(jwt.getIssuedAtAsInstant().isBefore(now));
        assertTrue(jwt.getExpiresAtAsInstant().isAfter(now));
        assertEquals(jwt.getIssuer(), issuer);
        // TODO to consider verifying token signature
    }

    @Given("the API for logger creation is defined")
    public void the_api_for_logger_creation_is_defined() {
        RestAssured.baseURI = SENSOR_BASE_URL;
        String apiKey = System.getenv("SKYCELL_API_KEY");

        assertNotNull(apiKey);
        assert (apiKey.length() > 0);
    }

    @When("a user makes API requests to create logger with type {string}")
    public void a_user_makes_api_requests_to_create_logger_with_type(String loggerType) {
        String randomHexString = RandomStringUtils.random(16, "0123456789ABCDEF");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("loggerNumber", randomHexString);
        requestBody.put("loggerType", loggerType);
        requestBody.put("baseInterval", 600);

        sensorResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("APIKEY", System.getenv("SKYCELL_API_KEY"))
                .body(requestBody)
                .when()
                .post("/v1/lora/configuration");
    }

    @Then("the system should respond with successful creation messages for each logger")
    public void the_system_should_respond_with_successful_creation_messages_for_each_logger() {
        assertEquals(sensorResponse.getStatusCode(), 201);
    }
}
