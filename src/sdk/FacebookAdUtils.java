package com.r2games.idlerancher;

import android.app.Activity;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;

import java.util.HashMap;
import java.util.Map;

public class FacebookAdUtils {
    private static String TAG = "FacebookAdUtils";
    private static Map<AdsUtils.LEVEL, RewardedVideoAd> rewardedAdMap = new HashMap<>();
    private static Map<AdsUtils.LEVEL, String>  adUnitIdMap = new HashMap<>();
    public static Activity activityContext;
    private boolean canGetReward = false;
    AdsListener adsListener = null;

    public void init(Activity activity) {
        activityContext = activity;
        AudienceNetworkAds.initialize(activity);
        adUnitIdMap.put(AdsUtils.LEVEL.TOP, "481691325740090_518217585420797");
        adUnitIdMap.put(AdsUtils.LEVEL.HIGH, "481691325740090_517686965473859");
        adUnitIdMap.put(AdsUtils.LEVEL.MIDDLE, " 481691325740090_518216835420872");
        adUnitIdMap.put(AdsUtils.LEVEL.LOW, "481691325740090_518142922094930");
        adUnitIdMap.put(AdsUtils.LEVEL.AUTO, " 481691325740090_518218205420735");
//        for (Map.Entry<AdsUtils.LEVEL, String> entry : adUnitIdMap.entrySet()) {
//            createAndLoadRewardedAd(entry.getKey());
//        }
        createAndLoadRewardedAd(AdsUtils.LEVEL.TOP);
    }

    public void setAdsListener(AdsListener listener) {
        this.adsListener = listener;
    }

    public void watchVideo(final AdsUtils.LEVEL level){
        activityContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "will watchVideo");
                RewardedVideoAd rewardedVideoAd = rewardedAdMap.containsKey(level) ? rewardedAdMap.get(level) : null;
                if(rewardedVideoAd == null || !rewardedVideoAd.isAdLoaded() || rewardedVideoAd.isAdInvalidated()) {
                    if (adsListener != null) {
                        adsListener.adWatchFailed(AdsUtils.AD_TYPE.Facebook, level);
                    }
                    Log.i(TAG, "can not watchVideo");
                    return;
                }
                Log.i(TAG, "show watchVideo");
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                rewardedVideoAd.show();
            }
        });
    }

    public void createAndLoadRewardedAd(final AdsUtils.LEVEL level) {
        Log.i(TAG, "createAndLoadRewardedAd: "+level);
        String adUnitId = adUnitIdMap.get(level);
        final RewardedVideoAd  rewardedVideoAd = new RewardedVideoAd(activityContext, adUnitId);
        rewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                Log.d(TAG, "onError " + level + " " + error.getErrorMessage());
                if (adsListener != null) {
                    adsListener.adLoadFailed(AdsUtils.AD_TYPE.Facebook, level);
                }
                int levelInt = level.ordinal()-1;
                if (levelInt == AdsUtils.LEVEL.NONE.ordinal()) {
                    return ;
                }
                createAndLoadRewardedAd(AdsUtils.LEVEL.values()[levelInt]);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                Log.d(TAG, "onAdLoaded " + level);
                rewardedAdMap.put(level, rewardedVideoAd);
                if (adsListener != null) {
                    adsListener.adLoadSuccess(AdsUtils.AD_TYPE.Facebook, level);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                Log.d(TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                Log.d(TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                Log.d(TAG, "Rewarded video completed!");
                canGetReward = true;
                // Call method to give reward
                // giveReward();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                if (adsListener != null) {
                    adsListener.adWatchSuccess(AdsUtils.AD_TYPE.Facebook, level, canGetReward);
                }
                canGetReward = false;
                rewardedAdMap.remove(level);
                createAndLoadRewardedAd(AdsUtils.LEVEL.TOP);
                Log.d(TAG, "Rewarded video ad closed!");
            }
        });
        rewardedVideoAd.loadAd();
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
