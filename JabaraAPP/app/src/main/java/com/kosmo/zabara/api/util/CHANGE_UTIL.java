package com.kosmo.zabara.api.util;

import java.text.DecimalFormat;

public class CHANGE_UTIL {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public static int wonToInt(String data){
        return Integer.parseInt(data.substring(0, data.length() - 1).replaceAll(",", ""));
    }
    public static int commaToInt(String data){
        return Integer.parseInt(data.replaceAll(",", ""));
    }

    public static String intToWon(String data){
        return  decimalFormat.format(Integer.parseInt(data))+"원";
    }
    public static String intToComma(String data){
        return  decimalFormat.format(Long.valueOf(data.replaceAll(",","")));
    }
}
