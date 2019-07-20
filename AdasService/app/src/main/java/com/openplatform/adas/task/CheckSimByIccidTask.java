package com.openplatform.adas.task;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.network.IHttpEngine;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;
import com.openplatform.adas.util.AdasPrefs;
import com.openplatform.adas.util.JsonUtil;
import com.openplatform.adas.AdasService.ILoginNotify;
import com.openplatform.adas.util.OpenPlatformPrefsKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/5/7 10:21
 * Description :
 */
public class CheckSimByIccidTask implements Runnable {
    private String iccid;
    private boolean isSuccess = false;
    private ILoginNotify iLoginNotify;

    public CheckSimByIccidTask(String iccid,ILoginNotify iLoginNotify) {
        this.iccid = iccid;
        this.iLoginNotify = iLoginNotify;
    }

    @Override
    public void run() {
        long timeOut = SystemClock.elapsedRealtime() + 3 * 60 * 1000L;
        do {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("iccid", iccid);
                Factory.get().getHttpEngine().OnPostRequest(UrlConstant.ICCIDSIMNO_URL,null,jsonObject.toString(),new ISuccessCallback() {
                    @Override
                    public void onSuccess(HashMap<String, String> result) {
                        String body = result.get(IHttpEngine.KEY_BODY);
                        Log.d("CheckSimByIccidTask", "IccidSimNo onSuccess---->body: " + body);
                        try {
                            if (!TextUtils.isEmpty(body)) {
                                JSONObject repObj = new JSONObject(body);
                                int code = JsonUtil.getInt(repObj, "code");
                                String simNo = JsonUtil.getString(repObj, "simNo");
                                Log.d("CheckSimByIccidTask", "IccidSimNo onSuccess---->code: " + code+"   simNo: "+simNo);
                                if (code == 0 && simNo != null && simNo.length() == 11) {
                                    final AdasPrefs prefs = Factory.get().getApplicationPrefs();
                                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO,simNo);
                                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,simNo);
                                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SERIALNO,simNo);
                                    isSuccess = true;
                                    iLoginNotify.OnLogin();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new IFailCallback() {
                    @Override
                    public void onFail(int errorCode, String errorStr) {
                        Log.e("CheckSimByIccidTask", "IccidSimNo  errorCode: " + errorCode + "   errorStr: " + errorStr);
                    }
                });

                if (!isSuccess) SystemClock.sleep(10000);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } while (!isSuccess && SystemClock.elapsedRealtime() < timeOut);
    }
}
