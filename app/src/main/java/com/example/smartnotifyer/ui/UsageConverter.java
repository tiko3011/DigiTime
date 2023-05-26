package com.example.smartnotifyer.ui;

public class UsageConverter {
    public static String convertMiliToString(long milliseconds){
        long minute = milliseconds / 60000;

        if (minute > 60){
            return String.valueOf(minute / 60) + " h " + String.valueOf(minute % 60) + " m";
        } else {
            return String.valueOf(minute + " m");
        }
    }

    public static String convertMinuteToString(long minute){
        if (minute > 60){
            return String.valueOf(minute / 60) + " h " + String.valueOf(minute % 60) + " m";
        } else {
            return String.valueOf(minute + " m");
        }
    }
    public static long convertStringToHour(String timeString){
        if (timeString.contains("h")) {
            // Format: Y h Z m
            String[] parts = timeString.split(" ");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[2]);
            return hours * 60L + minutes;
        } else {
            // Format: X m
            String minutesString = timeString.split(" ")[0];
            return Long.parseLong(minutesString);
        }
    }
}
