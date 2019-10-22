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
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dasheng.utils.JWebSClient;
import com.dasheng.utils.OkHttpUtil;
import com.dasheng.utils.StringUtils;

import net.sf.json.JSONObject;

import java.net.URI;

//import org.json.JSONException;
//import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static Context mContext = null;
    private LocationReceiver locationReceiver = null;
    private Button btnTest = null;
    private Button btnMsg = null;
    private static final int STATUS_MESSAGE = 0x00;
    private static final String websocket_url = "ws://192.168.31.211:8018/socket";
    private static final String webapi_url = "http://192.168.31.211:8018/webapi";
    private static String username = "dasheng";
    JWebSClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("location.reportsucc");
        registerReceiver(locationReceiver, filter);

        btnTest = findViewById(R.id.btnTest);
        btnMsg = findViewById(R.id.btnMsg);
        MainOnClickListener mainOnClickListener = new MainOnClickListener();
        btnTest.setOnClickListener(mainOnClickListener);
        btnMsg.setOnClickListener(mainOnClickListener);
    }

    private class MainOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnTest:
                    //打开socket连接
                    initSocketClient();
                    break;
                case R.id.btnMsg:
                    //发送消息
                    sendMsg("您好！");
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
        jsonObj.put("username", username);
        okHttp.post(webapi_url + "/login", jsonObj, new OkHttpUtil.NetworkCallback() {
            @Override
            public void successful(String str) {
                Log.d(TAG, "webapi：登录成功");
                sendMsg("您好！");
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
                System.out.println("收到：" + message);

                JSONObject jsonObj = JSONObject.fromObject(message);
                String cmd = StringUtils.safeString(jsonObj.get("cmd"));
                String data = StringUtils.safeString(jsonObj.get("data"));
                switch (cmd) {
                    case "CMD_LOGIN_RESP":
                        Log.e(TAG, "onMessage：" + data);
                        Looper.prepare();
                        Toast.makeText(mContext, data, Toast.LENGTH_LONG).show();
                        Looper.loop();// 进入loop中的循环，查看消息队列
                        break;
                    case "CMD_MSG_RESP":
                        Log.e(TAG, "onMessage：" + data);
                        Looper.prepare();
                        Toast.makeText(mContext, data, Toast.LENGTH_LONG).show();
                        Looper.loop();// 进入loop中的循环，查看消息队列
                        break;
                    default:
                        break;
                }
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
                        sendLogin();
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
            JSONObject json = new JSONObject();
            json.put("cmd", "CMD_MSG");
            json.put("data", msg);
            client.send(json.toString());
            Log.e(TAG, "发送的消息：" + msg);
        } else {
            Log.e(TAG, "未登录或登录超时");
            // Looper.prepare();
            Toast.makeText(mContext, "未登录或登录超时", Toast.LENGTH_LONG).show();
            // Looper.loop();// 进入loop中的循环，查看消息队列
        }
    }

    // 发送登录 指令
    private void sendLogin() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        }
        username = telephonyManager.getImei();

        JSONObject data = new JSONObject();
        data.put("username", username);

        JSONObject login = new JSONObject();
        login.put("cmd", "CMD_LOGIN");
        login.put("data", data);

        client.send(login.toString());
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
                    String msg = location.getProvider() + ":" + location.getLongitude() + "," + location.getLatitude();

                    // 发送定位信息
                    if (client != null && client.isOpen()) {
                        JSONObject json = new JSONObject();
                        json.put("cmd", "CMD_LOCATE");
                        json.put("data", msg);
                        client.send(json.toString());
                        msg = "发送定位=>" + msg;
                        Log.e(TAG, msg);
                    }
                    text1.append("\n" + msg);

                    ScrollView scrollview = findViewById(R.id.scrollview);
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
                }
            }
        }
    }
}
