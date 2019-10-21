package com.dasheng.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.dasheng.model.ServiceResult;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/4/17.
 * 获取用户的地理位置
 */
public class GPSUtils {
    private static final String TAG = "GPSUtils";

    private static GPSUtils instance;
    private Context mContext;
    private LocationManager locationManager;
    private String[] providerArr = {LocationManager.GPS_PROVIDER
            , LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER};
    private Integer index = 0;
    private boolean changeWay = true;

    private GPSUtils(Context context) {
        this.mContext = context;
    }

    public static GPSUtils getInstance(Context context) {
        if (instance == null) {
            instance = new GPSUtils(context);
        }
        return instance;
    }

    /**
     * 判断定位权限
     */
    public boolean checkPermission() {
        //获取Location
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "获取定位权限不足");
            return false;
        }
        return true;
    }

    /**
     * 获取经纬度
     *
     * @return
     */
    public String getLngAndLat(OnLocationResultListener onLocationResultListener) {
        mOnLocationListener = onLocationResultListener;

        // 判断定位权限
        checkPermission();

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);

        String locationProvider = providerArr[index];
        //如果不是Gps或Network
        if (!providers.contains(LocationManager.GPS_PROVIDER)
                && !providers.contains(LocationManager.NETWORK_PROVIDER)) {
//            Intent i = new Intent();
//            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            mContext.startActivity(i);

            if (mOnLocationListener != null) {
                ServiceResult sr = new ServiceResult();
                sr.setErrorCode(-1);
                sr.setErrorMsg("没有可用的定位提供器：" + Arrays.toString(providerArr));
                mOnLocationListener.onLocationResult(null, sr);
            }
            return null;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (mOnLocationListener != null) {
            ServiceResult sr = new ServiceResult();
            if (location == null) {
                sr.setErrorCode(-1);
                sr.setErrorMsg(providerArr[index] + "获取定位失败");
            }
            mOnLocationListener.onLocationResult(location, sr);
        }
        if (location == null) {
            // 切换定位方式（下次再尝试）
            changeWay = true;
            index = (index + 1) % 3;
            locationProvider = providerArr[index];
        }
        if (changeWay) {
            //监视地理位置变化
            removeListener();
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
            changeWay = false;
        }
        return null;
    }

    public LocationListener locationListener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "Provider Enabled：" + provider);
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "Provider Disabled：" + provider);
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (mOnLocationListener != null) {
                ServiceResult sr = new ServiceResult();
                if (location == null) {
                    sr.setErrorCode(-1);
                    sr.setErrorMsg(providerArr[index] + "更新定位失败");
                }
                mOnLocationListener.OnLocationChange(location, sr);
            }
        }
    };

    public void removeListener() {
        locationManager.removeUpdates(locationListener);
    }

    private OnLocationResultListener mOnLocationListener;

    public interface OnLocationResultListener {
        /**
         * 返回定位结果
         *
         * @param location 位置对象
         * @param sr       操作结果
         */
        void onLocationResult(Location location, ServiceResult sr);

        /**
         * 更新定位信息
         *
         * @param location 位置对象
         * @param sr       操作结果
         */
        void OnLocationChange(Location location, ServiceResult sr);
    }
}
