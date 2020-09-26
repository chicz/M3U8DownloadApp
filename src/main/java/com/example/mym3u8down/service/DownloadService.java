package com.example.mym3u8down.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.example.mym3u8down.MainActivity;
import com.example.mym3u8down.R;
import com.example.mym3u8down.listener.DownloadListener;
import com.example.mym3u8down.livedata.MyLivedataModel;
import com.example.mym3u8down.task.DownloadTask;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

public class DownloadService extends Service {

    public static final String CHANNEL_ONE_ID = "com.chicz.cn";
    public static final String CHANNEL_ONE_NAME = "chicz channel one";
    public static final String STATUS = "status";
    public static final String ACTION_NAME = "com.example.mym3u8down.downloat_status";

    private DownloadTask downloadTask;
    private String downloadUrl;
    //"yyyy-MM-dd_HH-mm-ss-SSS"
    private String lastFileName_prefix;
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification(lastFileName_prefix,progress));
            MyLivedataModel.getInstance().getProgress().postValue(progress);
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            //下载成功，前台服务通知关闭
            try {
                System.out.println("--------_____________---------");
                MyLivedataModel.getInstance().getStatus().postValue(MainActivity.SUCCESS);
                System.out.println("--------_____________---------");
            }catch (Exception e){
                System.out.println("--------_______catch______---------");
                e.printStackTrace();
                System.out.println("--------_______catch______---------");
            }
            System.out.println("============================================");
            stopForeground(true);
            getNotificationManager().notify(1, getNotification(lastFileName_prefix,-1));
            /*Looper.prepare();
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
            Looper.loop();*/
            //
        }

        @Override
        public void onFailed() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onCanceled() {
        }
    };

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public class DownloadBinder extends Binder{
        public void startDownload(String url,String filePath){
            if (downloadTask == null){
                downloadUrl = url;
                lastFileName_prefix = filePath;
                downloadTask = new DownloadTask(listener);
                String[] params = new String[2];
                params[0] = url;
                params[1] = filePath;
                downloadTask.execute(params);
                int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
                startForeground(NOTIFICATION_ID, getNotification(filePath,0));
            }
        }
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if(progress >= 0){
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel( CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        return builder.build();
    }
}
