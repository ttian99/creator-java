package com.r2games.idlerancher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnDeeplinkResponseListener;
import com.adjust.sdk.OnDeviceIdsRead;

import org.json.JSONException;
import org.json.JSONObject;

import static com.r2games.idlerancher.MyApplication.application;

public class AdjustSdkUtils {
    private static String TAG = "AdjustSdkUtils";
    private static String APP_TOKEN = "";

    public static void onApplicationCreate(Context context) {
        String appToken =  APP_TOKEN;
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION; // 环境模式
        AdjustConfig config = new AdjustConfig(context, appToken, environment);

        // 会话回调参数
//        Adjust.addSessionCallbackParameter("foo", "bar");
//        Adjust.removeSessionCallbackParameter("foo");
//        Adjust.resetSessionCallbackParameters();
        // 会话合作伙伴参数
//        Adjust.addSessionPartnerParameter("foo", "bar");
//        Adjust.removeSessionPartnerParameter("foo");
//        Adjust.resetSessionPartnerParameters();
        // 延迟启动
//         config.setDelayStart(5.5);
        // 禁用跟踪
//         Adjust.setEnabled(false);
        // 离线模式
//         Adjust.setOfflineMode(true);
        // 事件缓冲
//         config.setEventBufferingEnabled(true);
        // GDPR 的被遗忘权
//         Adjust.gdprForgetMe(context);
        // SDK签名
//         config.setAppSecret(secretId, info1, info2, info3, info4);
        // 后台跟踪
        config.setSendInBackground(true);
        // 预安装跟踪码
//        config.setDefaultTracker("{TrackerToken}");
        // 设定日志等级
        config.setLogLevel(LogLevel.DEBUG);
        // 设定推送标签
//        Adjust.setPushToken(pushNotificationsToken, context);


//        // 延迟深度链接场景
//        config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
//            @Override
//            public boolean launchReceivedDeeplink(Uri deeplink) {
//                if (shouldAdjustSdkLaunchTheDeeplink(deeplink)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        // 初始化
        Adjust.onCreate(config);


        // Google Play服务广告ID
        Adjust.getGoogleAdId(context, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                // ...
            }
        });
        // Amazon广告ID
        String amazonAdId = Adjust.getAmazonAdId(context);
        // Adjust设备ID(adid)(sdk >= 4.11.0)
        String adid = Adjust.getAdid();
        // 用户归因(sdk >= 4.11.0)
        AdjustAttribution attribution = Adjust.getAttribution();

        // 注册回调
        application.registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }

    public static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }
        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) { }
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }
        @Override
        public void onActivityDestroyed(Activity activity) { }
        @Override
        public void onActivityStarted(Activity activity) { }
        @Override
        public void onActivityStopped(Activity activity) { }
    }

    /** 打点记录事件 **/
    public static void traceEvent(final JSONObject data) throws JSONException {
        String type = "";
        String eventToken = "";
        try {
            eventToken = data.getString("eventToken");
            type = data.getString("type");
        } catch (Exception e) {
            Log.e(TAG, "traceEvent error: " + e.getMessage());
        }

        AdjustEvent event = new AdjustEvent(eventToken);
        if (type.equals("pay")) {
            Double price = data.getDouble("price");
            String currency = data.getString("currency");
            event.setRevenue(price, currency);
            String orderId = data.getString("orderId");
            if (!orderId.isEmpty()) {
                event.setOrderId(orderId);
            }
        }
        Log.d(TAG, "==============> traceEvent: " + eventToken);
//        // 回调参数
//        event.addCallbackParameter("key", "value");
//        event.addCallbackParameter("foo", "bar");
//        // 合作伙伴参数
//        event.addPartnerParameter("key", "value");
//        event.addPartnerParameter("foo", "bar");

        Adjust.trackEvent(event);
    }
}
