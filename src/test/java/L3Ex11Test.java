import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class L3Ex11Test {

    String url = "https://playground.learnqa.ru/api/homework_cookie";

    @Test
    public void getCookies() {
        Response response = RestAssured
                .get(url)
                .andReturn();
        response.print();

        Map<String, String> cookies = response.getCookies();
        for (String key : cookies.keySet()) {
            String cookieName = key;
            System.out.println(cookieName);
            String cookieValue = response.getCookie(cookieName);
            System.out.println(cookieValue);

            assertTrue(cookies.containsKey(key));
            assertEquals(cookieValue, response.getCookie(cookieName));
        }
    }
}
