package com.dasheng.gooday;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dasheng.model.ReceivedMsg;
import com.dasheng.utils.GsonManager;
import com.dasheng.utils.JWebSClient;
import com.dasheng.utils.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private LocationReceiver locationReceiver = null;
    private Button btnTest = null;
    private static final int STATUS_MESSAGE = 0x00;
    private static final String websocket_url = "ws://192.168.31.211:8018/socket";
    private static final String webapi_url = "http://192.168.31.211:8018/webapi";
    private static String username = "dasheng";
    JWebSClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("location.reportsucc");
        registerReceiver(locationReceiver, filter);

        btnTest = findViewById(R.id.btnTest);
        MainOnClickListener mainOnClickListener = new MainOnClickListener();
        btnTest.setOnClickListener(mainOnClickListener);
    }

    private class MainOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnTest:
                    //登录系统
                    loginSystem();
                    break;
                default:
                    break;
            }
        }
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String mess = (String) msg.obj;
            switch (msg.what) {
                case STATUS_MESSAGE:
                    // setListSent(mess, ContextMsg.TYPE_RECEIVED);
                    break;
            }
        }
    };

    // 登录系统
    private void loginSystem() {
        OkHttpUtil okHttp = OkHttpUtil.getInstance();
        JSONObject jsonObj = new JSONObject();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        }
        username = telephonyManager.getImei();
        try {
            jsonObj.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        okHttp.post(webapi_url + "/login", jsonObj, new OkHttpUtil.NetworkCallback() {
            @Override
            public void successful(String str) {
                Log.d(TAG, "webapi：登录成功");
                //打开socket连接
                initSocketClient();
            }

            @Override
            public void error(String str) {
                Log.e(TAG, "webapi：登录失败：" + str);
            }
        });
    }

    //打开socket连接
    private void initSocketClient() {
        URI uri = URI.create(websocket_url);
        client = new JWebSClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e(TAG, "onMessage：" + message);
                ReceivedMsg receivedMsg = GsonManager.getGson(message, ReceivedMsg.class);
                Message msg = new Message();
                msg.what = STATUS_MESSAGE;
                msg.obj = receivedMsg.getMsg().getContent();
                mhandler.sendMessage(msg);
            }
        };
        connect();
    }

    /**
     * 连接
     */
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    client.connectBlocking();
                    Log.e(TAG, "connectBlocking：连接成功");

                    if (client.isOpen()) {
                        senJsonInit();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Socket：断开连接异常");
        } finally {
            client = null;
        }
    }

    /**
     * 发送消息
     */
    private void sendMsg(String msg) {
        if (null != client) {
            client.send(msg);
            Log.e(TAG, "发送的消息：" + msg);
        }
    }

    private void senJsonInit() {
        try {
            JSONObject msg = new JSONObject();
            msg.put("from", 2);

            JSONObject init = new JSONObject();
            init.put("type", "init");
            init.put("msg", msg);

            sendMsg(init.toString());
            Log.e(TAG, "Init初始化成功");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendJsonObject(String context) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("to", 1);
            msg.put("content", context);
            msg.put("from", 2);
            msg.put("headimg", "");

            JSONObject toOne = new JSONObject();
            toOne.put("type", "msg");
            toOne.put("msg", msg);
            sendMsg(toOne.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
                if (locationObj != null) {
                    Location location = (Location) locationObj;
                    TextView text1 = findViewById(R.id.text1);
                    text1.append("\n" + location.getProvider() + ": " + location.getLongitude() + " , " + location.getLatitude());

                    ScrollView scrollview = findViewById(R.id.scrollview);
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
                }
            }
        }
    }
}
