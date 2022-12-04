package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String urlBase = "https://playground.learnqa.ru/api/user/";
    private final String urlLogin = "https://playground.learnqa.ru/api/user/login";

    @Test
    public void testEditJustCreatedUser() {

        //generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(urlBase)
                .jsonPath();

        String userId = responseCreateAuth.getString("id");


        //login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post(urlBase + "login")
                .andReturn();

        //edit
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put(urlBase + userId)
                .andReturn();

        //get
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get(urlBase + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testNegativeEditWithoutAuth() {
        //create new user to take his id
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(urlBase, userData);
        int userId = responseCreateAuth.jsonPath().getInt("id");

        //try to edit just created user without authorization
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEdit = apiCoreRequests.makePutRequest(urlBase + userId, "token", "cookie", editData);
        Assertions.assertResponseCodeEquals(responseEdit, 400);
    }

    @Test
    public void testNegativeEditWithNotThatUserAuth() {
        //create a new user to take his id for edit
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(urlBase, userData);
        int userIdForEdit = responseCreateAuth.jsonPath().getInt("id");

        //create a new user to take his id for login
        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Response responseCreateAuth1 = apiCoreRequests.makePostRequest(urlBase, userData1);
        int userIdForAuth = responseCreateAuth1.jsonPath().getInt("id");

        //authorize as the second user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData1.get("email"));
        authData.put("password", userData1.get("password"));
        Response responseAuth = apiCoreRequests.makePostRequest(urlLogin, authData);
        String header = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");

        //try to edit the first user being authorized as the second one
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEdit = apiCoreRequests.makePutRequest(urlBase + userIdForEdit, header, cookie, editData);
        responseEdit.print();

        //for a bug investigation check the second user data
        Response responseUserData = apiCoreRequests.makeGetRequest(
                urlBase + userIdForAuth,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"));
        System.out.println(responseUserData.jsonPath().getString("firstName"));

//        Assertions.assertJsonByName(responseUserData, "firstName", newName);

        Assertions.assertResponseCodeEquals(responseEdit, 400);
    }

    @Test
    public void testNegativeEditEmailToIncorrectValue() {
        //create a new user to take his id for edit
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(urlBase, userData);
        int userIdForEdit = responseCreateAuth.jsonPath().getInt("id");

        //authorize
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseAuth = apiCoreRequests.makePostRequest(urlLogin, authData);
        String header = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");

        //try to edit the user's email to incorrect format
        String incorrectEmail = "123example.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", incorrectEmail);
        Response responseEdit = apiCoreRequests.makePutRequest(urlBase + userIdForEdit, header, cookie, editData);
        responseEdit.print();

        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.assertResponseTextEquals(responseEdit, "Invalid email format");
    }

    @Test
    public void testNegativeEditNameToVeryShort() {
        //create a new user to take his id for edit
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest(urlBase, userData);
        int userIdForEdit = responseCreateAuth.jsonPath().getInt("id");

        //authorize
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseAuth = apiCoreRequests.makePostRequest(urlLogin, authData);
        String header = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");

        //try to edit the user's first name
        String incorrectName = "J";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", incorrectName);
        Response responseEdit = apiCoreRequests.makePutRequest(urlBase + userIdForEdit, header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.assertJsonHasKey(responseEdit, "error");
        Assertions.assertErrorTextEquals(responseEdit, "Too short value for field firstName");
    }
}
