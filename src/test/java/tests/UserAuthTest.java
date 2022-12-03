package tests;

import io.restassured.response.Response;
import lib.BaseTestCase;
import lib.Assertions;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {

    String header;
    String cookie;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String loginUrl = "https://playground.learnqa.ru/api/user/login";
    private final String authUrl = "https://playground.learnqa.ru/api/user/auth";

    @BeforeEach
    public void loginUser() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(loginUrl, authData);
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("This test successfully authorize a user by email and password")
    @DisplayName("Test positive auth user")
    public void testAuthUser() {

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(authUrl, this.header, this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("This test checks auth status without cookie or token")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "header"})
    public void testNegativeAuthUser(String condition) {

        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(authUrl, this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("header")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(authUrl, this.header);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition is unknown: " + condition);
        }

//        RequestSpecification spec = RestAssured.given();
//        spec.baseUri("https://playground.learnqa.ru/api/user/auth");
//        if (condition.equals("cookie")) {
//            spec.cookie("auth_sid", this.cookie);
//        } else if (condition.equals("header")) {
//            spec.header("x-csrf-token", this.header);
//        } else {
//            throw new IllegalArgumentException("Condition is unknown: " + condition);
//        }
//        Response responseForCheck = spec.get().andReturn();
//        Assertions.assertJsonByName(responseForCheck, "user_id", 0);
    }
}
