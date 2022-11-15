import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class L2Ex9Test {

    String getURL = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    String checkURL = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

    @Test
    public void passwordTest() {

        String[] passwordList = {"123456", "password", "123456789", "12345678", "qwerty", "adobe123[a]", "baseball",
                "qwerty123", "iloveyou", "1234567", "1234567890", "1q2w3e4r", "666666", "photoshop[a]", "111111",
                "1qaz2wsx", "admin", "abc123", "1234", "mustang", "121212", "starwars", "bailey", "access", "flower",
                "555555", "monkey", "lovely", "shadow", "ashley", "sunshine", "letmein", "dragon", "passw0rd",
                "7777777", "123123", "football", "12345", "michael", "login", "!@#$%^&*", "welcome", "654321",
                "jesus", "password1", "master", "hello", "charlie", "888888", "superman", "696969", "qwertyuiop",
                "hottie", "freedom", "aa123456", "princess", "qazwsx", "ninja", "azerty", "solo", "loveme", "whatever",
                "donald", "batman", "zaq1zaq1", "Football", "trustno1", "000000"};

        Map<String, String> data = new HashMap<>();
        data.put("login", "super_admin");

        for (int i = 0; i < passwordList.length; i++) {
            String pass = passwordList[i];
            data.put("password", pass);

            Response getCookieResponse = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post(getURL)
                    .andReturn();
            String cookie = getCookieResponse.getCookie("auth_cookie");

            Response checkCookie = RestAssured
                    .given()
                    .cookie("auth_cookie", cookie)
                    .when()
                    .get(checkURL)
                    .andReturn();
            String result = checkCookie.asString();

            if (!result.equals("You are NOT authorized")) {
                System.out.println("Your password is: " + pass + ". " + checkCookie.print());
                break;
            }
        }
    }
}
