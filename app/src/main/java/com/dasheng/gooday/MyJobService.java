package com.dasheng.gooday;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.dasheng.model.ServiceResult;
import com.dasheng.utils.GPSUtils;

import java.util.List;

public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";
    private static GPSUtils instGps;

    public static void startJob(Context context) {
        instGps = GPSUtils.getInstance(context);
        instGps.checkPermission();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(
                Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(10,
                new ComponentName(context.getPackageName(),
                        MyJobService.class.getName())).setPersisted(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0以上延迟1s执行
            builder.setMinimumLatency(2000);
        } else {
            //每隔1s执行一次job
            builder.setPeriodic(2000);
        }
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //Log.e(TAG,"开启job");
        //7.0以上轮询
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startJob(this);
        }

        //判断服务是否在运行
        boolean isForegroundServiceRun = isServiceRunning(this, ForegroundService.class.getName());
        if (!isForegroundServiceRun) {
            Log.e(TAG, "restart");
            // startService(new Intent(this, ForegroundService.class));
        } else {
            Log.e(TAG, "Running");
        }

        instGps.getLngAndLat(new GPSUtils.OnLocationResultListener(){
            @Override
            public void onLocationResult(Location location, ServiceResult sr) {
                if(sr.getErrorCode()==0) {
                    //输入经纬度
                    Log.d(TAG + " location-1", location.getProvider() + "：" + location.getLongitude() + " " + location.getLatitude() + "");

                    // 广播位置信息
                    Intent intent = new Intent();
                    intent.putExtra("location", location);
                    intent.setAction("location.reportsucc");
                    sendBroadcast(intent);
                }else{
                    Log.e(TAG, sr.getErrorMsg());
                }
            }

            @Override
            public void OnLocationChange(Location location, ServiceResult sr) {
                if(sr.getErrorCode()==0) {
                    Log.d(TAG + " location-2", location.getProvider() + "：" + location.getLongitude() + " " + location.getLatitude() + "");
                }else{
                    Log.e(TAG, sr.getErrorMsg());
                }
            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(10);
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            if (TextUtils.equals(runningService.service.getClassName(), serviceName)) {
                return true;
            }
        }

        return false;
    }

}
