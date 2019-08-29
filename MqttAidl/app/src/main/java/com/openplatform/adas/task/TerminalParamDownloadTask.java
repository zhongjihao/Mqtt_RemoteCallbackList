package com.openplatform.adas.task;

import android.os.Process;
import android.util.Log;

import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/23 10:21
 * Description :
 */
public class TerminalParamDownloadTask implements Runnable{
    private static final String TAG = "TerParamDownloadTask";
    private static final int DOWNLOAD_THREAD_PRIORITY = Process.THREAD_PRIORITY_URGENT_AUDIO;

    private ISuccessCallback iSuccessCallback;
    private IFailCallback iFailCallback;
    private String deviceCode;
    private String deviceVersion;
    private int platType;
    private int funcType;
    private String mToken;

    public TerminalParamDownloadTask(ISuccessCallback iSuccessCallback,IFailCallback iFailCallback,String token, String deviceCode,String deviceVersion,int platType,int funcType) {
        this.iSuccessCallback = iSuccessCallback;
        this.iFailCallback = iFailCallback;
        this.mToken = token;
        this.deviceCode = deviceCode;
        this.deviceVersion = deviceVersion;
        this.platType = platType;
        this.funcType = funcType;
    }

    @Override
    public void run() {
        Process.setThreadPriority(DOWNLOAD_THREAD_PRIORITY);
        Log.d(TAG,"E: run------>deviceCode: "+deviceCode+"  deviceVersion: "+deviceVersion+"   platType: "+platType+"   funcType: "+funcType);
        try {
            JSONObject object = new JSONObject();
            object.put("deviceCode", deviceCode);
            object.put("deviceVersion",deviceVersion);
            object.put("platType",String.valueOf(platType));
            object.put("funcType",String.valueOf(funcType));
            Factory.get().getHttpEngine().OnPostRequest(UrlConstant.DEVICE_PARAM_DOWNLOAD_URL,mToken,object.toString(),iSuccessCallback,iFailCallback);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(TAG,"X: run");
    }
}
