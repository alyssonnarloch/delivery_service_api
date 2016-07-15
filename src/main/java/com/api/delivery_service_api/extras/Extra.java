package com.api.delivery_service_api.extras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extra {

    public static boolean zipCodeValid(String zipCode) {
        Pattern pattern = Pattern.compile("[0-9]{5}-[0-9]{3}");
        Matcher m = pattern.matcher(zipCode);
        return m.matches();
    }

    public static Date toDate(String dateStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

}
