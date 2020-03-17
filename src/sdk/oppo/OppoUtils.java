package com.xync.nearme.gamecenter;

import android.util.Log;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.common.model.ApiResult;
import com.nearme.game.sdk.common.model.biz.ReqUserInfoParam;

import org.json.JSONException;
import org.json.JSONObject;

public class OppoUtils {

    public static OppoUtils mInstace;
    public static String TAG = "OppoUtils";

    public static String APP_SECRET= "";

    static AppActivity m_activity;

    public static OppoUtils getInstance() {
        if (null == mInstace) {
            mInstace = new OppoUtils();
        }
        return mInstace;
    }

    public void init(AppActivity activity) {
        m_activity = activity;
        GameCenterSDK.init(APP_SECRET, m_activity);
        Log.i(TAG, "init: ");
//        login(new JSONObject());
    }

    public void login(final JSONObject obj) {
        GameCenterSDK.getInstance().doLogin(m_activity, new ApiCallback() {
            @Override
            public void onSuccess(String resultMsg) {
                Log.i(TAG, "doLogin onSuccess: ");
                // 登录成功
                GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {
                    @Override
                    public void onSuccess(String resultMsg) {
                        Log.i(TAG, "doGetTokenAndSsoid onSuccess: ");
                        try {
                            JSONObject json = new JSONObject(resultMsg);
                            String token = json.getString("token");
                            String ssoid = json.getString("ssoid");
                            obj.put("code", 0);
                            obj.put("result", "SUCCESS");
                            obj.put("token", token);
                            obj.put("ssoid", ssoid);
                            NativeBridge.nativeToJs(obj.toString());
                            Log.i(TAG, "onSuccess: token:" + token + " ssoid:" + ssoid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(String resultMsg, int resultCode) {
                        Log.i(TAG, "doGetTokenAndSsoid onFailure: ");
                        try {
                            obj.put("code", -2);
                            obj.put("result", "GET_TOKEN_FAILED");
                            NativeBridge.nativeToJs(obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onFailure(String resultMsg, int resultCode) {
                Log.i(TAG, "doLogin onFailure: ");
                // 登录失败
                try {
                    obj.put("code", -1);
                    obj.put("result", "LOGIN_FAILED");
                    NativeBridge.nativeToJs(obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getUserInfo(String token, String ssoid) {
        GameCenterSDK.getInstance().doGetUserInfo(new ReqUserInfoParam(token, ssoid), new ApiCallback(){
            @Override
            public void onSuccess(String resultMsg) {
            }
            @Override
            public void onFailure(String resultMsg, int resultCode) {
            }
        });
    }

    public void jumpLeisureSubject() {
        GameCenterSDK.getInstance().jumpLeisureSubject();
    }

    public void doGetVerifiedInfo() {
        GameCenterSDK.getInstance().doGetVerifiedInfo(new ApiCallback() {
            @Override
            public void onSuccess(String resultMsg) {
                try {
                    //解析年龄（age）
                    int age = Integer.parseInt(resultMsg);
                    if (age < 18) {
                        //已实名但未成年，CP开始处理防沉迷
                    } else {
                        //已实名且已成年，尽情玩游戏吧
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String resultMsg, int resultCode) {
                if(resultCode == ApiResult.RESULT_CODE_VERIFIED_FAILED_AND_RESUME_GAME){
                    //实名认证失败，但还可以继续玩游戏
                }else if(resultCode == ApiResult.RESULT_CODE_VERIFIED_FAILED_AND_STOP_GAME){
                    //实名认证失败，不允许继续游戏，CP需自己处理退出游戏
                }
            }
        });
    }

}
