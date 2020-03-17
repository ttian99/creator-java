package com.r2games.idlerancher;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class AdsUtils {

    private static String TAG = "AdsUtils";
    private static AdsUtils mInstace = null;

    public enum LEVEL {
        NONE,
        AUTO,
        LOW,
        MIDDLE,
        HIGH,
        TOP;

        LEVEL() {
        }
    }
    public enum AD_TYPE {
        AdMob,
        Facebook;

        AD_TYPE() {
        }
    }


    AdMobUtils adMobUtils = new AdMobUtils();
    FacebookAdUtils facebookAdUtils = new FacebookAdUtils();

    public JSONObject watchVideoData = null;
    public JSONObject videoReadyCallback = null;
    public Timer videoLoadingTimer = null;

    public static AdsUtils getInstance() {
        if (null == mInstace) {
            mInstace = new AdsUtils();
        }
        return mInstace;
    }

    public void init(Activity activity) {
        adMobUtils.init(activity);
        facebookAdUtils.init(activity);
        initListener();
    }

    public void initListener() {
        AdsListener listener = (new AdsListener() {
            @Override
            public void adLoadSuccess(AD_TYPE type, LEVEL level) {
                Log.i(TAG, "adLoadSuccess: " + type + " " + level);
                if (videoReadyCallback != null) {
                    String keys[] = {"ready"};
                    String values[] = {"1"};
                    putDataToData(videoReadyCallback, keys, values);
                    NativeBridge.nativeToJs(videoReadyCallback.toString());
                    videoReadyCallback = null;
                }
                if (videoLoadingTimer != null) {
                    videoLoadingTimer.cancel();
                    videoLoadingTimer = null;
                }
            }

            @Override
            public void adLoadFailed(AD_TYPE type, LEVEL level) {
                Log.i(TAG, "adLoadFailed: " + type + " " + level);
            }

            @Override
            public void adWatchSuccess(AD_TYPE type, LEVEL level, boolean canGetReward) {
                if (watchVideoData == null)     return;
                if (!canGetReward) {
                    String keys[] = {"ret", "videoState"};
                    String values[] = {"200", "NO_COMPLETE"};
                    putDataToData(watchVideoData, keys, values);
                }
                else {
                    String keys[] = {"ret", "videoState"};
                    String values[] = {"100", "reward"};
                    putDataToData(watchVideoData, keys, values);
                }
                NativeBridge.nativeToJs(watchVideoData.toString());
                watchVideoData = null;
            }

            @Override
            public void adWatchFailed(AD_TYPE type, LEVEL level) {
                if (watchVideoData == null)     return;
                String keys[] = {"ret", "videoState"};
                String values[] = {"200", "donotPlay"};
                putDataToData(watchVideoData, keys, values);
                NativeBridge.nativeToJs(watchVideoData.toString());
                watchVideoData = null;
            }
        });
        facebookAdUtils.setAdsListener(listener);
        adMobUtils.setAdsListener(listener);
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

    public void watchVideo(final JSONObject data) {
        watchVideoData = data;
        LEVEL admobLevel = adMobUtils.getCanWatchLevel();
        LEVEL facebookLevel = facebookAdUtils.getCanWatchLevel();
        Log.i(TAG, "watchVideo " + facebookLevel + " " + admobLevel);
        if (facebookLevel.ordinal() >= admobLevel.ordinal()) {
            facebookAdUtils.watchVideo(facebookLevel);
        } else {
            adMobUtils.watchVideo(admobLevel);
        }
    }

    public boolean videoReady(final JSONObject data) {
        LEVEL admobLevel = adMobUtils.getCanWatchLevel();
        LEVEL facebookLevel = facebookAdUtils.getCanWatchLevel();
        Log.i(TAG, "videoReady: " + facebookLevel + " " + admobLevel);
        if (admobLevel != LEVEL.NONE || facebookLevel != LEVEL.NONE) {
            return true;
        }
        return false;
    }

    public void setVideoReadyCallback(final JSONObject data)  {
        videoReadyCallback = data;
        if (videoReady(null)) {
            String keys[] = {"ready"};
            String values[] = {"1"};
            putDataToData(videoReadyCallback, keys, values);
            NativeBridge.nativeToJs(videoReadyCallback.toString());
            videoReadyCallback = null;
            return;
        }
        LEVEL admobLevel = adMobUtils.getCanWatchLevel();
        LEVEL facebookLevel = facebookAdUtils.getCanWatchLevel();
        if (admobLevel == LEVEL.NONE) {
            adMobUtils.createAndLoadRewardedAd(LEVEL.TOP);
        }
        if (facebookLevel == LEVEL.NONE) {
            facebookAdUtils.createAndLoadRewardedAd(LEVEL.TOP);
        }
        try {
            double timeOut = data.getDouble("value");
            videoLoadingTimer = new Timer();
            videoLoadingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    NativeBridge.nativeToJs(videoReadyCallback.toString());
                    videoReadyCallback = null;
                    this.cancel();
                }
            }, (long)(timeOut*1000));
            Log.i(TAG, "setVideoReadyCallback: ");
        }
        catch (JSONException e) {
            Log.e(TAG, "setVideoReadyCallback: ", e);
        }
    }
}
