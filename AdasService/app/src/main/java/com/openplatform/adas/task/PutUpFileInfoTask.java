package com.openplatform.adas.task;

import android.os.Process;
import android.util.Log;

import com.google.gson.Gson;
import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.datamodel.PutUpFileInfo;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/6/03 10:21
 * Description :
 */
public class PutUpFileInfoTask implements Runnable{
    private static final String TAG = "PutUpFileInfoTask";
    private static final int SELFCHECK_THREAD_PRIORITY = Process.THREAD_PRIORITY_URGENT_AUDIO;

    private ISuccessCallback iSuccessCallback;
    private IFailCallback iFailCallback;
    private PutUpFileInfo putUpFileInfo;
    private String mToken;

    public PutUpFileInfoTask(ISuccessCallback iSuccessCallback,IFailCallback iFailCallback,String token, PutUpFileInfo putUpFileInfo) {
        this.iSuccessCallback = iSuccessCallback;
        this.iFailCallback = iFailCallback;
        this.mToken = token;
        this.putUpFileInfo = putUpFileInfo;
    }

    @Override
    public void run() {
        Process.setThreadPriority(SELFCHECK_THREAD_PRIORITY);
        String jsonRequest = new Gson().toJson(putUpFileInfo);
        Log.d(TAG,"E: run------>jsonRequest: "+jsonRequest);
        Factory.get().getHttpEngine().OnPostRequest(UrlConstant.PUT_UPFILEINFO_URL,mToken,jsonRequest,iSuccessCallback,iFailCallback);
        Log.d(TAG,"X: run");
    }
}
