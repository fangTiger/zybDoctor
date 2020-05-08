package com.zuojianyou.zybdoctor.units;

public class TimeUtils {

    public static String toNormMin(String time) {
        if (time == null || time.length() < 12) return "time wrong";
        StringBuilder sb = new StringBuilder();
        sb.append(time.substring(0, 4));
        sb.append("-");
        sb.append(time.substring(4, 6));
        sb.append("-");
        sb.append(time.substring(6, 8));
        sb.append(" ");
        sb.append(time.substring(8, 10));
        sb.append(":");
        sb.append(time.substring(10));
        return sb.toString();
    }

    public static String toNormDay(String time) {
        if (time == null || time.length() < 8) return "time wrong";
        StringBuilder sb = new StringBuilder();
        sb.append(time.substring(0, 4));
        sb.append("-");
        sb.append(time.substring(4, 6));
        sb.append("-");
        sb.append(time.substring(6));
        return sb.toString();
    }
}
