package ru.galaxy773.buyregions.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private static Calendar nextMidnight;

    private static void setMidnight() {
        (nextMidnight = Calendar.getInstance()).set(11, 0);
        nextMidnight.set(12, 0);
        nextMidnight.set(13, 0);
        nextMidnight.set(14, 0);
        nextMidnight.add(5, 1);
    }

    public static String getTimeleft() {
        long totalSecs = getTimeLeftInSeconds();
        int hours = (int)totalSecs / 3600;
        int minutes = (int)(totalSecs % 3600L) / 60;
        int seconds = (int)totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static int getTimeLeftInSeconds() {
        if (nextMidnight == null) {
            setMidnight();
        }

        int totalSecs = (int)((nextMidnight.getTimeInMillis() - System.currentTimeMillis()) / 1000L);
        if ((long)totalSecs <= 0L) {
            setMidnight();
        }

        return totalSecs;
    }

    public static String getTimeDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
