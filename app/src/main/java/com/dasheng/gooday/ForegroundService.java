package com.dasheng.gooday;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ForegroundService extends Service {
    public static Service instance = null;
    private final static int GRAY_SERVICE_ID = 10;
    MediaPlayer play;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        instance = this;
        Log.d("foreground life","onCreate");
        super.onCreate();

        play = MediaPlayer.create(this, R.raw.happy);
        Toast.makeText(this, "创建后台服务...", Toast.LENGTH_LONG).show();

        Log.d("version", Build.VERSION.SDK_INT+"");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("com.dasheng.gooday", "gooday",
                    NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, "com.dasheng.gooday")
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();
            startForeground(GRAY_SERVICE_ID, notification);*/
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //如果 18 以上的设备 启动一个Service startForeground给相同的id
            //然后结束那个Service
            startForeground(GRAY_SERVICE_ID, new Notification());
            startService(new Intent(this, InnnerService.class));
        } else {
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
    }

    public static class InnnerService extends Service {
        @Override
        public void onCreate() {
            super.onCreate();
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("foreground life","onStartCommand");
        super.onStartCommand(intent, flags, startId);
        //play.setLooping(false);
        //play.start();
        Toast.makeText(this, "启动后台服务程序", Toast.LENGTH_LONG ).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        instance = null;
        Log.d("foreground life","onDestroy");
        play.release();
        super.onDestroy();
        Toast.makeText(this, "销毁后台服务...", Toast.LENGTH_LONG).show();
    }
}
