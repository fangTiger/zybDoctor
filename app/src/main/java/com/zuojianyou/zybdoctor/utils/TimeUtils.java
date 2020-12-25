package com.zuojianyou.zybdoctor.utils;

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

    /**
     * 秒转时分秒
     * @param second
     * @return
     */
    public static String getFormatTime(Integer second) {

        if (second != null) {
            String num0 = NumFormat(0);
            if(second < 60) {//秒
                return num0 + ":" + NumFormat(second);
            }

            if(second < 3600) {//分

                return NumFormat(second / 60) + ":" + NumFormat(second % 60);
            }

            if(second< 3600 * 24) {//时

                return NumFormat(second/ 60 / 60) + ":" + NumFormat(second/ 60 % 60) + ":" + NumFormat(second% 60);
            }

            if(second>= 3600 * 24) {//天

                return NumFormat(second/ 60 / 60 /24) + "天" +NumFormat(second/ 60 / 60 % 24) + ":" + NumFormat(second/ 60 % 60) + ":" + NumFormat(second% 60);
            }
        }

        return "--";
    }

    /**
     * 格式化时间
     * @param sec
     * @return
     */
    private static String NumFormat(int sec) {
        if (String.valueOf(sec).length() < 2){
            return "0"+sec;
        }else {
            return String.valueOf(sec);
        }
    }
}
