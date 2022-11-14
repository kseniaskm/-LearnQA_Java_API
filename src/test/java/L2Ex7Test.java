import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class L2Ex7Test {

    @Test
    public void redirectTest() {

        String url = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode = 0;
        int counter = 0;

        while (statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            url = response.getHeader("location");
            statusCode = response.getStatusCode();
            counter++;
        }

        System.out.println("Number of redirects is " + counter);
    }
}
