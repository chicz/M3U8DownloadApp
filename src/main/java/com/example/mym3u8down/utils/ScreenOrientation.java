package com.example.mym3u8down.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;

public class ScreenOrientation {
    public final static int SENSOR = 0;//传感器
    public final static int SENSOR_LANDSCAPR = 1;//传感横向
    public final static int SENSOR_PORTRAIT = 2;//传感纵向
    public final static int LANDSCAPE = 3;//横向
    public final static int PORTRAIT = 4;//纵向
    public final static int REVERSE_LANDSCAPE = 5;//横向相反
    public final static int REVERSE_PORTRAIT = 6;//纵向相反

    /**
     * 设置当前屏幕方向为横屏
     */
    public static void setScreenOrientation(Activity activity, int orientation){
        if(orientation==SENSOR && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }else if(orientation==SENSOR_LANDSCAPR && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }else if(orientation==SENSOR_PORTRAIT && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (orientation==REVERSE_LANDSCAPE && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }else if(orientation==REVERSE_PORTRAIT && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }else if(orientation==LANDSCAPE && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else if(orientation==PORTRAIT && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
