package com.r2games.idlerancher;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

public class SDKUtils {
    private static AppActivity mActivity = null;
    private static String TAG = "SDKUtils";

    public void init(AppActivity activity){
        mActivity = activity;
        //=>SDK-init-Begin
//        UpSdkUtils.init(activity);
        //=>SDK-init-End
        AdsUtils.getInstance().init(activity);
    }

    public static void onDestroy() {
        //=>SDK-onDestroy-Begin

        //=>SDK-onDestroy-End
    }


    public static void onPause() {
        //=>SDK-onPause-Begin
//        UpSdkUtils.onPause();
        //=>SDK-onPause-End
//        AdMobUtils.pause();
    }

    public static void onResume() {
        //=>SDK-onResume-Begin
//        UpSdkUtils.onResume();
        //=>SDK-onResume-End
//        AdMobUtils.resume();
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        //=>SDK-onActivityResult-Begin

        //=>SDK-onActivityResult-End
    }

    public static void onNewIntent(Intent intent) {
        //=>SDK-onNewIntent-Begin

        //=>SDK-onNewIntent-End
    }

    public static void onStop() {
        //=>SDK-onStop-Begin

        //=>SDK-onStop-End
    }

    public static void onRestart() {
        //=>SDK-onRestart-Begin

        //=>SDK-onRestart-End
    }

    public static void onBackPressed() {
        //=>SDK-onBackPressed-Begin

        //=>SDK-onBackPressed-End
    }

    public static  void onConfigurationChanged(Configuration newConfig) {
        //=>SDK-onConfigurationChanged-Begin

        //=>SDK-onConfigurationChanged-End
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //=>SDK-onRestoreInstanceState-Begin

        //=>SDK-onRestoreInstanceState-End
    }


    public static void onSaveInstanceState(Bundle outState) {
        //=>SDK-onSaveInstanceState-Begin

        //=>SDK-onSaveInstanceState-End
    }

    public static void onStart() {
        //=>SDK-onStart-Begin

        //=>SDK-onStart-End
    }
}


