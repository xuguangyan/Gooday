package com.dasheng.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class FloatViewUtils {
    private static WindowManager wm;
    private static WindowManager.LayoutParams params;
    private static  Button btn_floatView;
    private static View mInView;

    public static View createFloatView(Context context, Integer layoutId)
    {
        if(layoutId==null) {
            btn_floatView = new Button(context);
            btn_floatView.setText("悬浮窗");
            mInView = btn_floatView;
        }else {
            LayoutInflater inflater = LayoutInflater.from(context);
            //加载需要的XML布局文件
            mInView = inflater.inflate(layoutId, null, false);
        }

        wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();

        // 设置window type

        if (Build.VERSION.SDK_INT >= 26 && context.getApplicationInfo().targetSdkVersion > 22) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        } else {
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", context.getPackageName()));
            if (permission || "Xiaomi".equals(Build.MANUFACTURER) || "vivo".equals(Build.MANUFACTURER)) {
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_TOAST;
            }

        }
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
         * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
         */

        // 设置悬浮窗的长得宽
        params.width = 100;
        params.height = 100;

        // 设置悬浮窗的Touch监听
        mInView.setOnTouchListener(new View.OnTouchListener()
        {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        // 更新悬浮窗位置
                        wm.updateViewLayout(mInView, params);
                        break;
                }
                return true;
            }
        });

        wm.addView(mInView, params);

        return mInView;
    }
}
