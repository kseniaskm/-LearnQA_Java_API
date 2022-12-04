package tests;

import io.restassured.response.Response;
import lib.BaseTestCase;
import lib.Assertions;
import lib.ApiCoreRequests;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    private final String baseUrl = "https://playground.learnqa.ru/api/user/";
    private final String loginUrl = "https://playground.learnqa.ru/api/user/login";
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testNegativeDeleteAdmin() {
        //authorize as an admin
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseAuth = apiCoreRequests.makePostRequest(loginUrl, authData);
        String header = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");

        //try to delete the admin :)
        Response responseDelete = apiCoreRequests.makeDeleteRequest(baseUrl+2, header, cookie);
        Assertions.assertResponseCodeEquals(responseDelete, 400);
        Assertions.assertResponseTextEquals(responseDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    public void testPositiveDeleteUser() {

        //create a new user to take his id for edit
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(baseUrl, userData);
        int userIdForDelete = responseCreateAuth.jsonPath().getInt("id");

        //authorize
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseAuth = apiCoreRequests.makePostRequest(loginUrl, authData);
        String header = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");

        //try to delete the user
        Response responseDelete = apiCoreRequests.makeDeleteRequest(baseUrl+userIdForDelete, header, cookie);

        //check if the user has been deleted
        Response responseUserData = apiCoreRequests.makeGetRequest(baseUrl+userIdForDelete, header, cookie);

        Assertions.assertResponseCodeEquals(responseDelete, 200);
        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    public void testNegativeDeleteWithNotThatUserAuth() {

        //create a new user to take his id for delete
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(baseUrl, userData);
        int userIdForDelete = responseCreateAuth.jsonPath().getInt("id");

        //create a new user to take his id for login
        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Response responseCreateAuth1 = apiCoreRequests.makePostRequest(baseUrl, userData1);
        int userIdForAuth = responseCreateAuth1.jsonPath().getInt("id");

        //authorize as the second user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData1.get("email"));
        authData.put("password", userData1.get("password"));
        Response responseAuth = apiCoreRequests.makePostRequest(loginUrl, authData);
        String header = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");

        //try to delete the first user being authorized as the second one
        Response responseDelete = apiCoreRequests.makeDeleteRequest(baseUrl+userIdForDelete, header, cookie);

        //for a bug investigation check the second user data
        Response responseUserData = apiCoreRequests.makeGetRequest(
                baseUrl + userIdForAuth,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");

        Assertions.assertResponseCodeEquals(responseDelete, 400); //there is a bug
    }
}
