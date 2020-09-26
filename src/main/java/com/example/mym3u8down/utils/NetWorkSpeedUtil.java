package com.example.mym3u8down.utils;

import android.content.Context;
import android.net.TrafficStats;

import com.example.mym3u8down.livedata.NetWorkSpeedModel;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class NetWorkSpeedUtil {

    public final static long gb = 1024*1024*1024;
    public final static long mb = 1024*1024;
    public final static long kb = 1024;

    private Context context;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public NetWorkSpeedUtil(Context context){
        this.context = context;
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            showNetSpeed();
        }
    };

    public void startShowNetSpeed(){
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
        new Timer().schedule(task, 1000, 1000); // 1s后启动任务，每1s执行一次

    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :TrafficStats.getTotalRxBytes();
    }

    private void showNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long totalSize =  nowTotalRxBytes - lastTotalRxBytes;
        long totalTime =  nowTimeStamp - lastTimeStamp;
        String speed = "当前网速：";
        if(totalSize*1000/totalTime > mb){
            speed += decimalFormat.format((double) totalSize*1000/totalTime/mb)+" Mb/s";
        }else if(totalSize*1000/totalTime > kb){
            speed += decimalFormat.format((double) totalSize*1000/totalTime/kb)+" Kb/s";
        }else {
            speed += decimalFormat.format((double) totalSize*1000/totalTime)+" B/s";
        }
        NetWorkSpeedModel.getInstance().getSpeed().postValue(speed);
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
    }
}
