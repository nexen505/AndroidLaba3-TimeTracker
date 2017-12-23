package com.komarov.androidlab3.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ilia on 19.11.2017.
 */

public class StringDateTime {
    private String date, time;
    public static final String
            DATE_DELIMITER = "/",
            TIME_DELIMITER = ":",
            DATE_PATTERN = "dd/MM/yyyy",
            TIME_PATTERN = "HH:mm",
            DATE_TIME_PATTERN = String.format("%s %s", DATE_PATTERN, TIME_PATTERN);

    public StringDateTime() {
    }

    public StringDateTime(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public StringDateTime(Date date) {
        final String s = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.US).format(date);
        final String[] strings = s.split(" ");
        this.date = strings[0];
        this.time = strings[1];
    }

    public StringDateTime(long timestamp) {
        this(new Date(timestamp));
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Date toDate() {
        return Utils.fromString(String.format("%s %s", date, time), DATE_TIME_PATTERN);
    }
}
