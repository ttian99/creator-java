package com.r2games.idlerancher;

public interface AdsListener {

    void adLoadSuccess(AdsUtils.AD_TYPE type, AdsUtils.LEVEL level);
    void adLoadFailed(AdsUtils.AD_TYPE type, AdsUtils.LEVEL level);
    public abstract void adWatchSuccess(AdsUtils.AD_TYPE type, AdsUtils.LEVEL level, boolean canGetReward);
    void adWatchFailed(AdsUtils.AD_TYPE type, AdsUtils.LEVEL level);
}
