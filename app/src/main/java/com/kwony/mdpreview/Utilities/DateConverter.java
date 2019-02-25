package com.kwony.mdpreview.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateConverter {
    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTimeString = simpleDateFormat.format(c.getTime());

        return currentDateTimeString;
    }
}
