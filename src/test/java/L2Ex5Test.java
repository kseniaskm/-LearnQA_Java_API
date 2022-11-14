import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class L2Ex5Test {

    @Test
    public void getJsonTest() {

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        response.prettyPrint();
        ArrayList messages = response.getJsonObject("messages");
        String message = messages.get(1).toString();
        System.out.println(message);

    }
}
