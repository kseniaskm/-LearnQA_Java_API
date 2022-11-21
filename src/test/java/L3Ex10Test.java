import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class L3Ex10Test {

    @Test
    public void stringTest() {
        String anyString = "Тест на короткую фразу";
        int length = anyString.length();

        assertTrue(length > 15);
    }
}
