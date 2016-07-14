package com.api.delivery_service_api.extras;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extra {

    public static boolean zipCodeValid(String zipCode) {
        Pattern pattern = Pattern.compile("[0-9]{5}-[0-9]{3}");
        Matcher m = pattern.matcher(zipCode);
        return m.matches();
    }

}
