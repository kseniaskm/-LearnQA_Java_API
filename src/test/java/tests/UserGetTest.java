package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String urlLogin = "https://playground.learnqa.ru/api/user/login";
    private final String urlGetData = "https://playground.learnqa.ru/api/user/";
    private final String urlRegister = "https://playground.learnqa.ru/api/user";

    @Test
    public void getUserDetailsAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasKeys(responseUserData, expectedFields);

//        Assertions.assertJsonHasKey(responseUserData, "username");
//        Assertions.assertJsonHasKey(responseUserData, "firstName");
//        Assertions.assertJsonHasKey(responseUserData, "lastName");
//        Assertions.assertJsonHasKey(responseUserData, "email");
    }

    @Test
    public void getUserDetailsAsOtherUser() {

        //create new user to take his id
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(urlRegister, userData);
        int userId = responseCreateAuth.jsonPath().getInt("id");

        //authorize as a known user, but not just created
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseGetAuth = apiCoreRequests.makePostRequest(urlLogin, authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //get data other user
        Response responseUserData = apiCoreRequests.makeGetRequest(urlGetData + userId, header, cookie);
        responseUserData.print();

        String[] unexpectedFields = {"firstName", "lastName", "email"};
        String expectedField = "username";

        Assertions.assertJsonHasKey(responseUserData, expectedField);
        Assertions.assertJsonHasNotKeys(responseUserData, unexpectedFields);
    }
}