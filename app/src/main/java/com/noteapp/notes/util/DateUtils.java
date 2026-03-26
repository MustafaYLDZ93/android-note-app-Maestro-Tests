package com.noteapp.notes.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMMM yyyy", new Locale("tr", "TR"));

    private static final SimpleDateFormat DATETIME_FORMAT =
            new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("tr", "TR"));

    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        return DATETIME_FORMAT.format(new Date(timestamp));
    }
}
