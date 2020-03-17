package com.r2games.idlerancher;

import android.util.Log;

import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.json.JSONException;
import org.json.JSONObject;

import static com.r2games.idlerancher.AppActivity.app;
import static java.lang.Thread.sleep;

public class NativeBridge {
    public static String TAG = "JsBridge";

    /** 测试专用 **/
    public static void ping(final JSONObject obj)  throws JSONException {
        Log.i(TAG, "test: " + obj.toString());
        obj.put("ping", "ok");
        nativeToJs(obj.toString());
    }
    /** 观看视频广告 **/
    public static void watchVideo(final JSONObject obj)  throws JSONException {
        Log.i(TAG, "watchVideo: " + obj.toString());
//        UpSdkUtils.watchVideo(obj);
        AdsUtils.getInstance().watchVideo(obj);
    }
    /** 打点上报 **/
    public static void report(final JSONObject obj) throws JSONException {
        Log.i(TAG, "report: " + obj.toString());
        AdjustSdkUtils.traceEvent(obj);
        TalkingDataSdkUtils.report(obj);
    }

    /** 视频广告是否加载完 **/
    public static boolean videoReady(final JSONObject obj)  throws JSONException {
        Log.i(TAG, "videoReady: " + obj.toString());
        return AdsUtils.getInstance().videoReady(obj);
    }

    public static void setVideoReadyCallback(final JSONObject obj)  throws JSONException {
        Log.i(TAG, "setVideoReadyCallback: " + obj.toString());
        AdsUtils.getInstance().setVideoReadyCallback(obj);
    }

    public static void pay(final String jsonStr)  throws JSONException {
        JSONObject obj = new JSONObject(jsonStr);
        obj.put("cn", "圣诞节看风景的司法机圣诞节反倒是咖啡机");
        try {
            sleep(1000);
        } catch (Exception e) {
            Log.e(TAG, "pay: sleep error", e);
        }
        String str = obj.toString();
        nativeToJs(str);
    }

    public static String getMethod(final String jsonStr) throws JSONException {
        Log.i(TAG, "getMethod: " + jsonStr);
        String str = "undefined";
        String ret = "undefined";
        String desc = "undefined";
        String value = "undefined";

        JSONObject obj = new JSONObject(jsonStr);
        String cmd  = obj.getString("cmd");

        if ( cmd.equals("xixi")) {
            value = null;
            ping(obj);
            ret = "0";
            desc = "success";
        } else if (cmd.equals("watchVideo")) {
            value = null;
            watchVideo(obj);
            ret = "0";
            desc = "success";
        } else if (cmd.equals("report")) {
            value = null;
            report(obj);
            ret = "0";
            desc = "success";
        } else if ( cmd.equals("pay")) {
            value = null;
            pay(jsonStr);
            ret = "0";
            desc = "success";
        } else if ( cmd.equals("videoReady")) {
            value = "0";
            if (videoReady(obj)) {
                value = "1";
            }
            ret = "0";
            desc = "success";
        } else if (cmd.equals("videoReadyCallback")) {
            setVideoReadyCallback(obj);
            value = null;
            ret = "0";
            desc = "success";
        } else{
            ret   = "1";
            desc  = "not found the method:" + cmd;
        }

        JSONObject objs = new JSONObject();
        objs.put("cmd", cmd);
        objs.put("ret", ret);
        objs.put("value", value);
        objs.put("desc", desc);

        str = objs.toString();

        return str;
    }

    public static String jsToNative(String jsonStr) {
        String str = "undefined";
        try {
            str = getMethod(jsonStr);
        } catch (JSONException e) { // TODO Auto-generated catch block
            e.printStackTrace();
            str = "trace error!";
        }
        Log.i(TAG, "jsToNative: str = " + str);
        return str;
    }

    public static void nativeToJs(final String jsonStr) {
        // 一定要在 GL 线程中执行
        app.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "nativeToJs -> run: jsonStr = " + jsonStr);
                    String shell = "cc.nat.nativeToJs(" + jsonStr + ")";
                    Cocos2dxJavascriptJavaBridge.evalString(shell);
                } catch(Exception e) {
                    Log.e(TAG, "run: error", e);
                }
            }
        });
    }
}
