package com.r2games.idlerancher;

import android.content.Context;
import android.util.Log;

import com.tendcloud.tenddata.TDGAAccount;
import com.tendcloud.tenddata.TalkingDataGA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.r2games.idlerancher.MyApplication.application;

/**
 * TalkingData SDK
 *
 * 文档：http://doc.talkingdata.com/posts/64
 */
public class TalkingDataSdkUtils {
    private static String TAG = "TalkingDataSdkUtils";
    private static String APPID = ""; // 您的 App ID
    private static String PLAFORM_ID = "play.google.com"; // 渠道 ID

    public static void init(Context context) {
        // App ID: 在TalkingData Game Analytics创建应用后会得到App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TalkingDataGA.init(context, APPID, PLAFORM_ID);
        setAccount();
    }

    // 设置账号
    public static void setAccount() {
        TDGAAccount account = TDGAAccount.setAccount(TalkingDataGA.getDeviceId(application));
        account.setAccountType(TDGAAccount.AccountType.ANONYMOUS);
    }

    /** 设置任务、关卡、副本 */
    //接受或进入
    public static void onBegin(String missionId) {}
    //任务完成
    public static void onCompleted(String missionId) {}
    //任务失败
    public static void onFailed(String missionId, String cause){}

    /**
     * 自定义事件
     * 注：在某 key 的 value 取值较离散情况下，不要直接填充具体数值，而应划分区间后传入，否则value不同取值很可能超过平台最大数目限制，而影响最终展示数据的效果。 如：示例中金币数可能很离散，请先划分合适的区间。
     */
    public static void onEvent(String eventId, final Map<String, Object> eventData) {
        TalkingDataGA.onEvent (eventId, eventData);
    }


    /** 上报数据 **/
    public static void report(JSONObject jsonObj) throws JSONException {
        Log.i(TAG, "report: " + jsonObj.toString());
        // 获取时间名称
        String eventId = jsonObj.getString("event");
        // 转换JSONObject为Map
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> sIterator = jsonObj.keys();
        while(sIterator.hasNext()){
            // 获得key
            String key = sIterator.next();
            // 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
            String value = jsonObj.getString(key);
            map.put(key, value);
            System.out.println("key: "+key+",value"+value);
        }
        onEvent(eventId, map);
    }
}
