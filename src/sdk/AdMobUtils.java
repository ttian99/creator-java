package com.r2games.idlerancher;

import android.app.Activity;
import android.util.Log;

import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;


import java.util.Map;
import java.util.HashMap;


public class AdMobUtils {

    private static String TAG = "AdMobUtils";
    private static String APP_ID = "";
    public static Activity activityContext;

    private static Map<AdsUtils.LEVEL, RewardedAd>  rewardedAdMap = new HashMap<>();
    private static Map<AdsUtils.LEVEL, String>  adUnitIdMap = new HashMap<>();
    private boolean canGetReward = false;
    AdsListener adsListener = null;

    public void init(Activity activity) {
        activityContext = activity;
//        MediationTestSuite.launch(activity, APP_ID);
        MobileAds.initialize(activity, APP_ID);
        adUnitIdMap.put(AdsUtils.LEVEL.TOP, "ca-app-pub-0000000000000000000/0000000000");
        adUnitIdMap.put(AdsUtils.LEVEL.HIGH, "ca-app-pub-0000000000000000000/0000000000");
        adUnitIdMap.put(AdsUtils.LEVEL.MIDDLE, "ca-app-pub-0000000000000000000/0000000000");
        adUnitIdMap.put(AdsUtils.LEVEL.LOW, "ca-app-pub-0000000000000000000/0000000000");
        adUnitIdMap.put(AdsUtils.LEVEL.AUTO, "ca-app-pub-0000000000000000000/0000000000");
//        adUnitIdMap.put("test", "ca-app-pub-0000000000000000000/0000000000");
//        for (Map.Entry<AdsUtils.LEVEL, String> entry : adUnitIdMap.entrySet()) {
//            createAndLoadRewardedAd(entry.getKey());
//        }
        createAndLoadRewardedAd(AdsUtils.LEVEL.TOP);
    }

    public void setAdsListener(AdsListener listener) {
        this.adsListener = listener;
    }

    public void watchVideo(final AdsUtils.LEVEL level)  {
        activityContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            RewardedAd rewardedAd = rewardedAdMap.containsKey(level) ? rewardedAdMap.get(level) : null;
            if (rewardedAd != null && rewardedAd.isLoaded()) {
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    public void onRewardedAdOpened() {
                        // 此处为广告展示的回调
                        Log.i("AdMobUtils", "onRewardedAdOpened: 此处为广告展示的回调");
                    }

                    public void onRewardedAdClosed(){
                        // 此处为广告关闭的回调
                        Log.i("AdMobUtils", "onRewardedAdClosed: 此处为广告关闭的回调");
                        if (adsListener != null) {
                            adsListener.adWatchSuccess(AdsUtils.AD_TYPE.AdMob, level, canGetReward);
                        }
                        canGetReward = false;
                        rewardedAdMap.remove(level);
                        createAndLoadRewardedAd(AdsUtils.LEVEL.TOP);
                    }

                    public void onUserEarnedReward( RewardItem reward) {
                        // 此处为广告可以发放奖励的回调
                        Log.i("AdMobUtils", "onUserEarnedReward: 此处为广告可以发放奖励的回调");
                        canGetReward = true;
                    }

                    public void onRewardedAdFailedToShow(int errorCode) {
                        // 此处为广告广告播放失败的回调
                        Log.i("AdMobUtils", "onRewardedAdFailedToShow: 此处为广告可以发放奖励的回调");
                        if (adsListener != null) {
                            adsListener.adWatchFailed(AdsUtils.AD_TYPE.AdMob, level);
                        }
                        rewardedAdMap.remove(level);
                        createAndLoadRewardedAd(AdsUtils.LEVEL.TOP);
                    }
                };
                rewardedAd.show(activityContext, adCallback);
            } else {
                if (adsListener != null) {
                    adsListener.adWatchFailed(AdsUtils.AD_TYPE.AdMob, level);
                }
                Log.d(TAG, "The rewarded ad wasn't loaded yet." + level);
                createAndLoadRewardedAd(AdsUtils.LEVEL.TOP);
            }
            }
        });
    }

    //加载广告
    public RewardedAd createAndLoadRewardedAd(final AdsUtils.LEVEL level) {
        if (level == AdsUtils.LEVEL.NONE) {
            return null;
        }
        Log.i(TAG, "createAndLoadRewardedAd: " + level);
        final String adUnitId = adUnitIdMap.get(level);
        final RewardedAd rewardedAd = new RewardedAd(activityContext, adUnitId);
        final RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.i(TAG, "onRewardedAdLoaded: " + adUnitId +  " " + level);
                rewardedAdMap.put(level, rewardedAd);
                if (adsListener != null) {
                    adsListener.adLoadSuccess(AdsUtils.AD_TYPE.AdMob, level);
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                Log.i(TAG, "onRewardedAdFailedToLoad: " + adUnitId + " " + level + " " + errorCode);
                if (adsListener != null) {
                    adsListener.adLoadFailed(AdsUtils.AD_TYPE.AdMob, level);
                }
                int levelInt = level.ordinal()-1;
                if (levelInt == AdsUtils.LEVEL.NONE.ordinal()) {
                    return ;
                }
                createAndLoadRewardedAd(AdsUtils.LEVEL.values()[levelInt]);
            }
        };
        activityContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
            }
        });
        return rewardedAd;
    }

    AdsUtils.LEVEL getCanWatchLevel() {
        AdsUtils.LEVEL l = AdsUtils.LEVEL.NONE;
        for (AdsUtils.LEVEL level : AdsUtils.LEVEL.values()) {
            if (rewardedAdMap.containsKey(level)) {
                if (level.ordinal() > l.ordinal()) {
                    l = level;
                }
            }
        }
        return l;
    }

}
