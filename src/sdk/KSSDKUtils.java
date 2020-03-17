package com.r2games.idlerancher;

import android.app.Activity;
import android.util.Log;

import com.kwai.allin.ad.ADApi;
import com.kwai.allin.ad.ADConstant;
import com.kwai.allin.ad.ADHandler;
import com.kwai.allin.ad.OnADSceneListener;
import com.kwai.allin.ad.Param;
import com.kwai.allin.ad.Position;
import com.kwai.opensdk.allin.client.AllInSDKClient;
import com.kwai.opensdk.allin.client.Report;
import com.kwai.opensdk.allin.client.listener.AllInInitListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class KSSDKUtils {
    private static String TAG = "KSSDKUtils";
    private static KSSDKUtils mInstace = null;
    Activity activity = null;

    JSONObject watchVideoData = null;
    ADHandler adHandler = null;
    boolean canReward = false;
    boolean playingVideo = false;

    public static KSSDKUtils getInstance() {
        if (null == mInstace) {
            mInstace = new KSSDKUtils();
        }
        return mInstace;
    }

    public void init(Activity activity) {
        this.activity = activity;
        AllInSDKClient.init(true, new AllInInitListener() {
            @Override
            public void onSuccess(String s) {
                loadRewardVideoAd();
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public boolean isGameBattleStatus() {
                return false;
            }
        }, "IdleRancher", null);
        this.initAdParam();
        ADApi.getApi().init(activity);
    }

    public void watchVideo(final JSONObject data)throws JSONException {
        this.watchVideoData = data;
        if (adHandler == null || playingVideo) {
            String keys[] = {"ret", "videoState"};
            String values[] = {"200", "have not video"};
            putDataToData(watchVideoData, keys, values);
            NativeBridge.nativeToJs(watchVideoData.toString());
            watchVideoData = null;
            adHandler = null;
            playingVideo = false;
            this.loadRewardVideoAd();
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playingVideo = true;
                adHandler.show(activity);
            }
        });
    }

    public void initAdParam() {
//        //添加 sigmob
//        Param param = new Param("1282", "27531c7c64157934", ADApi.CHANNEL_SIGMOB);
//        param.setRewardVideoId("e01480dcea2");
//        ADApi.getApi().addParams(param);
//
//        //添加 穿山甲
        Param param = new Param("5029258", "", ADApi.CHANNEL_PANGOLIN);
//        param.setBannerId("");//901121895  //Banner
//        param.setInteractId("");		//插屏
        param.setVideoId("929258543");			//全屏视频
        param.setRewardVideoId("929258796");		//激励视频
//        param.setBannerHeight(90);//banner 的期望高度
//        param.setBannerWidth(600);//banner 的期望宽度
//        param.setInteractWidth(3); // 插屏的 宽高比例
//        param.setInteractHeight(3);//
        param.setBannerPosition(new Position(ADConstant.AD_BANNER_LOCATION_BOTTOM));//设置 banner 位置
        ADApi.getApi().addParams(param);

        //添加快手广告
        param = new Param("90165", "放置牧语", ADApi.CHANNEL_KWAI);
        param.setRewardVideoId("90165001");
//        param.setVideoPortrait(true);//是否竖屏 不设置则展示的时候自动判断
        ADApi.getApi().addParams(param);

//        param = new Param("1101152570", "", ADApi.CHANNEL_GDT);
//        param.setBannerId("2.0版本广告位");//UNIFIED_BANNER 2.0 版
//        param.setRewardVideoId("广告位");//支持竖版出横版视频
//        param.setInteractId("广告位");//只小规格

        ADApi.getApi().setDefault(ADApi.CHANNEL_PANGOLIN);
    }

    public void loadRewardVideoAd() {
        Map<String, String> map = new HashMap<>();
        int type = ADConstant.AD_TYPE_REWARD_VIDEO;
        map.put(ADConstant.AD_CHANNEL_KWAI, "90165001");
        map.put(ADConstant.AD_CHANNEL_PANGOLIN, "929258796");
//        map.put(ADConstant.AD_CHANNEL_GDT, "填写对应的广告位");
//        map.put(ADConstant.AD_CHANNEL_SIGMOB, "填写对应的广告位");
        ADApi.getApi().loadADAutoByType(type, map, null, new OnADSceneListener() {
            @Override
            public void onAdReward(String s, String s1, int i, String s2) {
                Log.i(TAG, "onAdReward: " + "slotId " + s + " channel " + s1 + " code" + i + " msg " + s2);
                canReward = true;
            }

            @Override
            public void onAdCompletion(String s, int i, String s1) {
                Log.i(TAG, "onAdCompletion: ");
            }

            @Override
            public void onAdLoad(String s, String s1, int i, String s2, ADHandler handler) {
                Log.i(TAG, "onAdLoad: " + i + " " + s1);
                if (i == 0) {
                    adHandler = handler;
                } else {
                    loadRewardVideoAd();
                }
            }

            @Override
            public void onAdShow(String s, String s1) {
                Log.i(TAG, "onAdShow: ");
            }

            @Override
            public void onAdClick(String s, String s1) {
                Log.i(TAG, "onAdClick: ");
            }

            @Override
            public void onAdClose(String s, String s1) {
                Log.i(TAG, "onAdClose: ");
                if (canReward) {
                    String keys[] = {"ret", "videoState"};
                    String values[] = {"100", "reward"};
                    putDataToData(watchVideoData, keys, values);
                } else {
                    String keys[] = {"ret", "videoState"};
                    String values[] = {"200", "NO_COMPLETE"};
                    putDataToData(watchVideoData, keys, values);
                }
                NativeBridge.nativeToJs(watchVideoData.toString());
                canReward = false;
                adHandler = null;
                watchVideoData = null;
                loadRewardVideoAd();
                playingVideo = false;
            }
        });
    }

    public boolean isVideoReady() {
        return adHandler != null;
    }

    public void putDataToData(JSONObject obj, String keys[], String values[]) {
        if (keys.length != values.length) {
            Log.i(TAG, "putDataToData: keys.length is not equal to values.length");
            return;
        }
        try {
            for (int i = 0; i < keys.length; i++) {
                obj.put(keys[i], values[i]);
            }
        } catch (JSONException e) {
            Log.e(TAG, "putDataToData json error: ", e);
        }
    }

    public void setVideoReadyCallback(final JSONObject data) throws JSONException {
        if (isVideoReady()) {
            String keys[] = {"ready"};
            String values[] = {"1"};
            putDataToData(data, keys, values);
            NativeBridge.nativeToJs(data.toString());
            return;
        }
        double timeOut = data.getDouble("value");
        Timer videoLoadingTimer = new Timer();
        videoLoadingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isVideoReady()) {
                    String keys[] = {"ready"};
                    String values[] = {"1"};
                    putDataToData(data, keys, values);
                    NativeBridge.nativeToJs(data.toString());
                } else {
                    NativeBridge.nativeToJs(data.toString());
                }
                this.cancel();
            }
        }, (long)(timeOut*1000));
        Log.i(TAG, "setVideoReadyCallback: ");
    }


    public void report(JSONObject jsonObj) throws JSONException {
//        Log.i(TAG, "report: " + jsonObj.toString());
//        // 获取事件名称
//        String eventId = jsonObj.getString("event");
//        // 转换JSONObject为Map
//        Map<String, String> map = new HashMap<>();
//        Iterator<String> sIterator = jsonObj.keys();
//        while(sIterator.hasNext()){
//            // 获得key
//            String key = sIterator.next();
//            // 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
//            String value = jsonObj.getString(key);
//            map.put(key, value);
//            System.out.println("key: "+key+",value"+value);
//        }
//        Report.report(eventId, map);
    }
}
