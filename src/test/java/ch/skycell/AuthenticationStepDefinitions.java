package ch.skycell;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationStepDefinitions {
    private final String AUTH_BASE_URL = "https://keycloak.dev.skycell.ch";
    private Response authResponse;
    private String accessToken;
    private String username;
    private String password;

    public Response request_to_authenticate(String username, String password) {
        RestAssured.baseURI = AUTH_BASE_URL;

        return authResponse = RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam("client_id", "webapp")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/realms/skycell/protocol/openid-connect/token");
    }

    @Given("credentials are definied")
    public void credentials_are_definied() {
        username = System.getenv("SKYCELL_USERNAME");
        password = System.getenv("SKYCELL_PASSWORD");

        assertNotNull(username);
        assertNotNull(password);
        assert(!username.isEmpty());
        assert(!password.isEmpty());
    }

    @When("sending a request to authenticate")
    public void sending_a_request_to_authenticate() {
        request_to_authenticate(username, password);
    }

    @Then("the response contains authentication token")
    public void the_response_contains_authentication_token() {
        JsonPath jsonPath = new JsonPath(authResponse.asString());
        accessToken = jsonPath.getString("access_token");

        assertEquals(200, authResponse.getStatusCode());
        assertNotNull(accessToken);
        assert (!accessToken.isEmpty());
    }

    @Then("the token is a valid JWT token")
    public void the_token_is_a_valid_jwt_token() {
        String issuer = "https://keycloak.dev.skycell.ch/realms/skycell";
        Instant now = Instant.now();
        DecodedJWT jwt = JWT.decode(accessToken);

        assertTrue(jwt.getExpiresAtAsInstant().isAfter(now));
        assertEquals(jwt.getIssuer(), issuer);
        // TODO to consider verifying token signature
    }

    @Given("password is incorrect")
    public void password_is_incorrect() {
        request_to_authenticate(username, "notCorrectPass11");
    }

    @Then("user not authenticated")
    public void user_not_authenticated() {
        JsonPath jsonPath = new JsonPath(authResponse.asString());
        String error_description = jsonPath.getString("error_description");

        assertEquals(401, authResponse.getStatusCode());
        assertEquals("Invalid user credentials", error_description);
    }
}
