package com.dasheng.utils;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSClient extends WebSocketClient {

    public JWebSClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("JWebSClient", "连接打开onOpen");
    }

    @Override
    public void onMessage(String message) {
        Log.e("JWebSClient", message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("JWebSClient", "关闭 断开连接onClose");
    }

    @Override
    public void onError(Exception ex) {
        Log.e("JWebSClient", "错误 onError");
    }
}