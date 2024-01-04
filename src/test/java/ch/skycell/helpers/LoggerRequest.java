package ch.skycell.helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

public class LoggerRequest {
    protected String generateLoggerNumber() {
        return  RandomStringUtils.random(16, "0123456789ABCDEF");
    }
    protected Response createLogger(String loggerType, String loggerNumber) {
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
}
