package com.linkitsoft.beepvending.Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommonUtils {
    public static String getDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }


    public static String getDateToday() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss aaa");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }
    public static String formatTwoDecimal(double value) {
        return String.format("%,.2f", value);
    }
}
