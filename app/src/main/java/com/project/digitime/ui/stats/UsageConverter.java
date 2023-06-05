package com.project.digitime.ui.stats;

import com.project.digitime.database.Stat;

import java.text.DecimalFormat;
import java.util.List;

public class UsageConverter {
    public static DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public static String convertMilliToString(long milliseconds){
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

    public static List<Stat> deleteDuplicates(List<Stat> stats){

        for (int i = 0; i < stats.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (stats.get(i).statName.equals(stats.get(j).statName)){
                    stats.remove(j);
                    break;
                }
            }
        }

        return stats;
    }
}
