import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class L2Ex8Test {

    String url = "https://playground.learnqa.ru/ajax/api/longtime_job";

    @Test
    public void tokenTest() throws InterruptedException {

        JsonPath getTokenResponse = RestAssured
                .given()
                .when()
                .get(url)
                .jsonPath();
        String token = getTokenResponse.getString("token");
        int timeoutMs = getTokenResponse.getInt("seconds") * 1000;

        if (token != null) {
            JsonPath getJobResponse = RestAssured
                    .given()
                    .param("token", token)
                    .when()
                    .get(url)
                    .jsonPath();
            String status = getJobResponse.getString("status");
            System.out.println("\nFirst status is " + status);

            if (status.equals("Job is NOT ready")) {
                Thread.sleep(timeoutMs);
                getJobResponse = RestAssured
                        .given()
                        .param("token", token)
                        .when()
                        .get(url)
                        .jsonPath();
                status = getJobResponse.getString("status");
                System.out.println("\nSecond status is " + status);
            }

            if (status.equals("Job is ready")) {
                String result = getJobResponse.getString("result");
                System.out.println("\nJob result is " + result);
            }
        }
    }
}
