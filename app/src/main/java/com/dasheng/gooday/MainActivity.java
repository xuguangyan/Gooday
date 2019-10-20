package com.dasheng.gooday;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("main life", "onCreate");

        super.onCreate(savedInstanceState);
        intent = new Intent(MainActivity.this, ForegroundService.class);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("com.dasheng.gooday", "gooday",
                NotificationManager.IMPORTANCE_LOW);
        manager.createNotificationChannel(channel);

//        Notification notification = new NotificationCompat.Builder(this, "com.dasheng.gooday")
//                .setContentTitle("通知标题")
//                .setContentText("通知内容..")
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setDefaults(Notification.DEFAULT_SOUND).build();
//        manager.notify(10, notification);

        // startService(intent);
        MyJobService.startJob(this);
        startActivity(new Intent(this,HomeActivity.class));
    }

    @Override
    public void onDestroy() {
        Log.d("main life", "onDestroy");
        // startService(intent);
        super.onDestroy();
    }
}
