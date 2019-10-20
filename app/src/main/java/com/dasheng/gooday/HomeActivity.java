package com.dasheng.gooday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private LocationReceiver locationReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("location.reportsucc");
        registerReceiver(locationReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(locationReceiver);
        super.onDestroy();
    }

    //内部类，实现BroadcastReceiver
    public class LocationReceiver extends BroadcastReceiver {
        //必须要重载的方法，用来监听是否有广播发送
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction.equals("location.reportsucc")) {
                Object locationObj = intent.getExtras().get("location");
                if(locationObj!=null) {
                    Location location = (Location)locationObj;
                    TextView text1 = findViewById(R.id.text1);
                    text1.append("\n" + location.getLongitude() + " , " + location.getLatitude());
                }
            }
        }
    }
}
