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
public class ServerParamDownloadTask implements Runnable{
    private static final String TAG = "ServerParamDownloadTask";
    private static final int DOWNLOAD_THREAD_PRIORITY = Process.THREAD_PRIORITY_URGENT_AUDIO;

    private ISuccessCallback iSuccessCallback;
    private IFailCallback iFailCallback;
    private String deviceCode;
    private String deviceVersion;
    private String mToken;

    public ServerParamDownloadTask(ISuccessCallback iSuccessCallback,IFailCallback iFailCallback,String token, String deviceCode,String deviceVersion) {
        this.iSuccessCallback = iSuccessCallback;
        this.iFailCallback = iFailCallback;
        this.mToken = token;
        this.deviceCode = deviceCode;
        this.deviceVersion = deviceVersion;
    }

    @Override
    public void run() {
        Process.setThreadPriority(DOWNLOAD_THREAD_PRIORITY);
        Log.d(TAG,"E: run------>deviceCode: "+deviceCode+"  deviceVersion: "+deviceVersion);
        try {
            JSONObject object = new JSONObject();
            object.put("deviceCode", deviceCode);
            object.put("deviceVersion",deviceVersion);
            Factory.get().getHttpEngine().OnPostRequest(UrlConstant.SERVER_PARAM_DOWNLOAD_URL,mToken,object.toString(),iSuccessCallback,iFailCallback);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(TAG,"X: run");
    }
}
