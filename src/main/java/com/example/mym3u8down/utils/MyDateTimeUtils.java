package com.example.mym3u8down.utils;

public class MyDateTimeUtils {

    public static String videoSedFormat(int second){
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        if(0!=hh){
            return String.format("%02d:%02d:%02d", hh, mm, ss);
        }else{
            return String.format("%02d:%02d", mm, ss);
        }
    }

}
