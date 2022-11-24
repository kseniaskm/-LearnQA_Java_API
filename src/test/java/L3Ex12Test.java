import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class L3Ex12Test {

    String url = "https://playground.learnqa.ru/api/homework_header";

    @Test
    public void getHeaders() {
        Response response = RestAssured
                .get(url)
                .andReturn();

        List<Header> headerList = response.getHeaders().asList();

        for (Header header : headerList) {
            String headerName = header.getName();
            System.out.println("There is a header name: " + headerName);
            String headerValue = header.getValue();
            System.out.println("There is a header value: " + headerValue);

            assertTrue(response.getHeaders().hasHeaderWithName(headerName));
            assertEquals(headerValue, response.getHeader(headerName));
        }
    }
}
