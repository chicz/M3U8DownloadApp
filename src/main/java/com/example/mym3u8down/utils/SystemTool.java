package com.example.mym3u8down.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

public class SystemTool {

    /*
    * 识别用户点击的区域
    * */
    public static boolean isShouldHideKeyboard(List<View> views, MotionEvent event){
        for (View v : views){
            if(v!=null){
                int[] l = new int[2];
                v.getLocationInWindow(l);
                int left=l[0], top=l[1], bottom=top+v.getHeight(), right=left+v.getWidth();
                if(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom){
                    //是EditText区域，不隐藏
                    return false;
                }
            }
        }
        return true;
    }

    /*
    * 隐藏键盘
    * */
    public static void hideInputKeyboard(Context context, List<View> viewList){
        if (viewList == null) return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        for(View v : viewList){
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /*
    * 弹出键盘
    * */
    public static void showInputKeyboard(Context context, View v){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }
}
