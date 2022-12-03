package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

    public static void assertJsonByName(Response Response, String name, int expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to the expected value");
    }

    public static void assertJsonByName(Response Response, String name, String expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to the expected value");
    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer) {
        assertEquals(expectedAnswer, Response.asString(), "Response is not expected");
    }

    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode) {
        assertEquals(expectedStatusCode, Response.getStatusCode(), "Status code is not expected");
    }

    public static void assertJsonHasKey(Response Response, String expectedKey) {
        Response.then().assertThat().body("$", hasKey(expectedKey));
    }

    public static void assertJsonHasKeys(Response Response, String[] expectedKeys) {
        for (String expectedKey : expectedKeys) {
            Assertions.assertJsonHasKey(Response, expectedKey);
        }
    }

    public static void assertJsonHasNotKey(Response Response, String unexpectedKey) {
        Response.then().assertThat().body("$", not(hasKey(unexpectedKey)));
    }
}