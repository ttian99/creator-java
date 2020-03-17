package com.r2games.idlerancher;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class r2Utils {
    private static String TAG = "r2Utils";

    /** 监听按键 **/
    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", "你确定退出吗?");
                obj.put("sure", "确定");
                obj.put("cancel", "取消");
//                com.ltc.ltcUtils.exit(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /** 获取设备的IMEI号码 **/
    public static String getImei() {
        String imei = "";
//        try {
//            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (telephonyManager != null) {
//                String imei;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    imei = telephonyManager.getImei();
//                }
//                else {
//                    imei = telephonyManager.getDeviceId();
//                }
//                return imei;
//            }
//            return imei;
//        } catch (e) {
//            print
//        }
        return imei;
    }

    /**
     * 获取Android ID
     * 表示一个64位的数字，在设备第一次启动的时候随机生成并在设备的整个生命周期中不变。（如果重新进行出厂设置可能会改变）
     */
    public static String getAndriodId(Context context) {
        String androidId = "";
        try{
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        Log.i("ANDROID_ID", androidId + " ");
        return androidId;
    }

}
