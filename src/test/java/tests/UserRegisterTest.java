package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest {

    private final String url = "https://playground.learnqa.ru/api/user/";
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testNegativeCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(url)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testPositiveCreateNewUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post(url)
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasKey(responseCreateAuth, "id");
    }

    @Test
    public void testCreateNewUserWithIncorrectEmail() {
        String email = "123example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreate = apiCoreRequests.makePostRequest(url, userData);
        Assertions.assertResponseCodeEquals(responseCreate, 400);
        Assertions.assertResponseTextEquals(responseCreate, "Invalid email format");
    }

    @Test
    public void testNegativeCreateUserWithShortName() {
        String username = "J";
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);
        Response responseCreate = apiCoreRequests.makePostRequest(url, userData);
        responseCreate.print();
        Assertions.assertResponseCodeEquals(responseCreate, 400);
        Assertions.assertResponseTextEquals(responseCreate, "The value of 'username' field is too short");
    }

    @Test
    public void testNegativeCreateUserWithLongName() {
        String username = "Jqjgnpfvykqsmakahhorprrdemzaycnlbckriecvqkiljnauaxdgmwbxllecihoaisgbqditfqgjttpjvhzbhnloqttsujqdojizrzwzaauapnshpxpbzpxftguqfnexjnmeebjbykwbtgjrshptsblmkpbtsnzcxwqrubvirxkcrupwotwxsdeqpjcxmkbsufurfrplybzzoyiemlavvjmoknhkuhaygeyqrezqbpcexsrybqhbuxmgqwi";
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);
        Response responseCreate = apiCoreRequests.makePostRequest(url, userData);
        responseCreate.print();
        Assertions.assertResponseCodeEquals(responseCreate, 400);
        Assertions.assertResponseTextEquals(responseCreate, "The value of 'username' field is too long");
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testNegativeCreateUserWithoutParameter(String param){
        Map<String, String> userData = new HashMap<>();
        userData.put(param, null);
        userData = DataGenerator.getRegistrationData(userData);
        Response responseCreate = apiCoreRequests.makePostRequest(url, userData);
        responseCreate.print();
        Assertions.assertResponseCodeEquals(responseCreate, 400);
        Assertions.assertResponseTextEquals(responseCreate, "The following required params are missed: " + param);
    }
}
