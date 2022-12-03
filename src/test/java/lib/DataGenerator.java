package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {

    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        String email = "learnqa" + timestamp + "@example.com";
        return email;
    }

    public static Map<String, String> getRegistrationData() {
        Map<String, String> regData = new HashMap<>();
        regData.put("email", DataGenerator.getRandomEmail());
        regData.put("password", "123");
        regData.put("username", "learnqa");
        regData.put("firstName", "learnqa");
        regData.put("lastName", "learnqa");

        return regData;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        Map<String, String> defaultValues = DataGenerator.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};

        for (String key : keys) {
            if (nonDefaultValues.containsKey(key)) {
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }
}
