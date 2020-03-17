package com.r2games.idlerancher;

//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//
//import com.up.ads.UPAdsSdk;
//import com.up.ads.UPRewardVideoAd;
//import com.up.ads.wrapper.video.UPRewardVideoAdListener;
//import com.up.ads.wrapper.video.UPRewardVideoLoadCallback;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import static android.Manifest.permission.READ_PHONE_STATE;
//import static android.Manifest.permission.REQUEST_INSTALL_PACKAGES;
//import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//import static com.up.ads.unity.BaseProxy.showInterstitialDebugActivity;
//import static com.up.ads.unity.BaseProxy.showVideoDebugActivity;

/**
 *  UpSdkUtils
 *
 */
//public class UpSdkUtils {
//    private static String TAG = "UpSdkUtils";
//    // 视频广告实例
//    public static UPRewardVideoAd mVideoAd = null;
//
//    /** sdk初始化 **/
//    public static void init(Activity activity) {
//        /********DEBUG******* *************
//         * 发布时一定要关闭 *
//         * ******************************/
////        setDebug(true);
//
//        /** 设定国内/海外 **/
//        UPAdsSdk.UPAdsGlobalZone platform = UPAdsSdk.UPAdsGlobalZone.UPAdsGlobalZoneForeign;
//        /** 国内sdk特殊初始化 **/
//        if (platform == UPAdsSdk.UPAdsGlobalZone.UPAdsGlobalZoneDomestic) {
//            initDomestic(activity);
//        }
//        // 请在主 Activity 中尽早初始化广告 SDK，并根据游戏发行区域分别传入对应的 UPAdsGlobalZone 参数。
//        UPAdsSdk.init(activity, platform);
//        // 视频实例初始化
//        initVideoAd(activity);
//    }
//
//    private static void setDebug(Boolean isDebug) {
//        if (isDebug){
//            //打开插屏调试页面
//            showInterstitialDebugActivity();
//            //打开激励视频调试页面
//            showVideoDebugActivity();
//        }
//        Log.d(TAG, "setDebug: " + isDebug.toString());
//        UPAdsSdk.setDebuggable(isDebug);
//    }
//
//    /** 国内sdk版本初始化 **/
//    public static void initDomestic(Activity activity) {
//        // 申请动态权限 (该事项仅适用于 Android 国内版本，如使用 Android 海外版本，请忽略此处。)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                    || ContextCompat.checkSelfPermission(activity, REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED
//                    || ContextCompat.checkSelfPermission(activity, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE, REQUEST_INSTALL_PACKAGES, READ_PHONE_STATE}, 001);
//            }
//        }
//        // 设置用户id
//        //   对于国内发行的产品，由于无法正常收集 GAID，导致用户新增计算错误，
//        //   3.0.03 及以上版本开始增加 UPAdsSdk.setCustomerId(String customerId) 方法，
//        //   customerId 参数取 AndroidId 的值。请在 UPAdsSdk.init() 之前调用此方法。
//        String andoridId = r2Utils.getAndriodId(activity);
//        UPAdsSdk.setCustomerId(andoridId);
//    }
//
//    public static void onPause() {
//        UPAdsSdk.onApplicationPause();
//    }
//
//    public static void onResume() {
//        UPAdsSdk.onApplicationResume();
//    }
//
//    public static void initVideoAd(Activity activity) {
//        // 初始化
//        mVideoAd = UPRewardVideoAd.getInstance(activity);
//        // 调试模式 - 激励视频广告的使用情况
//        if (mVideoAd != null && UPAdsSdk.isDebuggable()) {
//            mVideoAd.showVideoDebugActivity(activity);
//        }
//        loadVideo();
//    }
//
//    public static void loadVideo() {
//        mVideoAd.load(new UPRewardVideoLoadCallback() {
//            @Override
//            public void onLoadFailed() {
//                // code
//                // 激励视频加载失败，请等待加载成功
//                Log.i(TAG, "onLoadFailed: 激励视频加载失败，请等待加载成功");
//            }
//            @Override
//            public void onLoadSuccessed() {
//                // code
//                // 激励视频加载成功，可以展示
//                Log.i(TAG, "onLoadSuccessed: 激励视频加载成功，可以展示");
//            }
//        });
//    }
//
//    public static void watchVideo(final JSONObject data) throws JSONException {
//        String cpPlaceId = data.getString("cpPlaceId");
//        mVideoAd.setUpVideoAdListener(new UPRewardVideoAdListener() {
//            @Override
//            public void onVideoAdClicked() {
//                // 此处为广告点击的回调
//                Log.i(TAG, "onVideoAdClicked: 此处为广告点击的回调");
//                try {
//                    data.put("videoState", "clicked");
//                } catch (Exception e) {
//                    Log.e(TAG, "onVideoAdClicked: ", e);
//                }
//            }
//            @Override
//            public void onVideoAdClosed() {
//                // 此处为广告关闭的回调
//                Log.i(TAG, "onVideoAdClosed: 此处为广告关闭的回调");
//                try {
//                    data.put("videoState", "clicked");
//                } catch (Exception e) {
//                    Log.e(TAG, "onVideoAdClosed: ", e);
//                }
//            }
//            @Override
//            public void onVideoAdDisplayed() {
//                // 此处为广告展示的回调
//                Log.i(TAG, "onVideoAdDisplayed: 此处为广告展示的回调");
//                try {
//                    data.put("videoState", "displayed");
//                } catch (Exception e) {
//                    Log.e(TAG, "onVideoAdDisplayed: ", e);
//                }
//            }
//            @Override
//            public void onVideoAdReward() {
//                // 此处为广告可以发放奖励的回调
//                Log.i(TAG, "onVideoAdReward: 此处为广告可以发放奖励的回调");
//                try {
//                    data.put("ret", "100");
//                    data.put("videoState", "reward");
//                    NativeBridge.nativeToJs(data.toString());
//                } catch (Exception e) {
//                    Log.e(TAG, "onVideoAdReward: ", e);
//                }
//            }
//            @Override
//            public void onVideoAdDontReward(String reason) {
//                // 此处为广告观看不符合条件，不发放奖励的回调，一般是因为观看视频时间短
//                Log.i(TAG, "onVideoAdDontReward: 此处为广告可以发放奖励的回调");
//                try {
//                    data.put("ret", "200");
//                    data.put("videoState", "dontReward");
//                    NativeBridge.nativeToJs(data.toString());
//                } catch (Exception e) {
//                    Log.e(TAG, "onVideoAdDontReward: ", e);
//                }
//            }
//        });
//
//        if (mVideoAd != null ) {
//            if (mVideoAd.isReady()) {
//                mVideoAd.show(cpPlaceId);
//            } else {
//                try {
//                    data.put("ret", "-1");
//                    data.put("videoState", "notReady");
//                    NativeBridge.nativeToJs(data.toString());
//                } catch (Exception e) {
//                    Log.e(TAG, "onVideoAdDontReward: ", e);
//                }
//            }
//        }
//    }

//}
