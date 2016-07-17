package com.api.delivery_service_api.extras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.ws.rs.WebApplicationException;

public class DateParam {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private java.sql.Date date;

    public DateParam(String dateStr) throws WebApplicationException {
        try {
            date = new java.sql.Date(df.parse(dateStr).getTime());
        } catch (final ParseException ex) {
            date = null;
        }
    }

    public java.sql.Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        if (date != null) {
            return date.toString();
        } else {
            return "";
        }
    }
}
