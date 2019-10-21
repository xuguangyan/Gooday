package com.dasheng.gooday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dasheng.utils.FloatViewUtils;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    private Context mContext = null;
    private LocationReceiver locationReceiver = null;
    private Button btnCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("location.reportsucc");
        registerReceiver(locationReceiver, filter);

        btnCamera = (Button) findViewById(R.id.btnCamera);
        MainOnClickListener mainOnClickListener = new MainOnClickListener();
        btnCamera.setOnClickListener(mainOnClickListener);
    }

    private class MainOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCamera:
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Settings.canDrawOverlays(mContext)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, 1);
                        } else {
                            startActivity(new Intent(HomeActivity.this, CameraActivity.class));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
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
                    text1.append("\n" + (new Date()).getTime()+  ", " + location.getLongitude() + " , " + location.getLatitude());

                    ScrollView scrollview = findViewById(R.id.scrollview);
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
                }
            }
        }
    }
}
