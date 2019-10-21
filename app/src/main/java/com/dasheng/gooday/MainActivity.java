package com.dasheng.gooday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ForegroundService.instance != null) {
            startActivity(new Intent(this,HomeActivity.class));
            return ;
        }
        Log.d("main life", "onCreate");

        super.onCreate(savedInstanceState);

//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(this, "com.dasheng.gooday")
//                .setContentTitle("通知标题")
//                .setContentText("通知内容..")
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setDefaults(Notification.DEFAULT_SOUND).build();
//        manager.notify(10, notification);

        intent = new Intent(MainActivity.this, ForegroundService.class);
        startService(intent);
        MyJobService.startJob(this);

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(intent, 1);
//            } else {
//                Context appContext = getApplicationContext();
//                FloatViewUtils.createFloatView(appContext);
//            }
//        }
        startActivity(new Intent(this,HomeActivity.class));
    }

    @Override
    public void onDestroy() {
        Log.d("main life", "onDestroy");
        // startService(intent);
        super.onDestroy();
    }
}
