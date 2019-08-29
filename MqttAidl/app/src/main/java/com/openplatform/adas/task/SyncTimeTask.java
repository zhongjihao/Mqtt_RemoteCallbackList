package com.openplatform.adas.task;

import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.network.IHttpEngine;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;
import com.openplatform.aidl.GetDatTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/5/7 10:21
 * Description :
 */
public class SyncTimeTask implements Runnable {
    private boolean isSuccess = false;

    public SyncTimeTask() {

    }

    @Override
    public void run() {
        long timeOut = SystemClock.elapsedRealtime() + 15 * 60 * 1000L;

        do {
            Factory.get().getHttpEngine().OnGetRequest(UrlConstant.GETDATTIME_URL, null, null, new ISuccessCallback() {
                @Override
                public void onSuccess(HashMap<String, String> result) {
                    String body = result.get(IHttpEngine.KEY_BODY);
                    Log.d("SyncTimeTask", "onSuccess---->body: " + body);
                    try {
                        if (!TextUtils.isEmpty(body)) {
                            GetDatTime response = new Gson().fromJson(body, GetDatTime.class);
                            Log.d("SyncTimeTask", "onSuccess---->response: " + response.toString());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String timestamp = dateFormat.format(new Date(response.getData().getNowTime()));
                            Log.d("SyncTimeTask", "onSuccess---->timestamp: " + timestamp);
                            Date date = dateFormat.parse(timestamp);
                            AlarmManager am = (AlarmManager) Factory.get().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            am.setTime(date.getTime());
                            isSuccess = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new IFailCallback() {
                @Override
                public void onFail(int errorCode, String errorStr) {
                    Log.e("SyncTimeTask", "errorCode: " + errorCode + "   errorStr: " + errorStr);
                }
            });

            if (!isSuccess) SystemClock.sleep(30000);
        } while (!isSuccess && SystemClock.elapsedRealtime() < timeOut);
    }
}
