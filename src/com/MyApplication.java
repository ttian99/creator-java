package com.r2games.idlerancher;

import android.app.Application;

public class MyApplication extends Application {
    public static Application application = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        TalkingDataSdkUtils.init(this);
        AdjustSdkUtils.onApplicationCreate(this);
    }
}
