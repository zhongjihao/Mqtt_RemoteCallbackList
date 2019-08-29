package com.openplatform.adas.task;

import android.os.Process;
import android.util.Log;

import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;
import com.google.gson.Gson;
import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.aidl.SelfCheck;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/19 10:21
 * Description :
 */
public class SelfCheckTask implements Runnable {
    private static final String TAG = "SelfCheckTask";
    private static final int SELFCHECK_THREAD_PRIORITY = Process.THREAD_PRIORITY_URGENT_AUDIO;

    private ISuccessCallback iSuccessCallback;
    private IFailCallback iFailCallback;
    private SelfCheck mSelfCheck;
    private String mToken;

    public SelfCheckTask(ISuccessCallback iSuccessCallback,IFailCallback iFailCallback,String token, SelfCheck selfCheck) {
        this.iSuccessCallback = iSuccessCallback;
        this.iFailCallback = iFailCallback;
        this.mToken = token;
        this.mSelfCheck = selfCheck;
    }

    @Override
    public void run() {
        Process.setThreadPriority(SELFCHECK_THREAD_PRIORITY);
        String jsonRequest = new Gson().toJson(mSelfCheck);
        Log.d(TAG,"E: run------>jsonRequest: "+jsonRequest);
        Factory.get().getHttpEngine().OnPostRequest(UrlConstant.SELF_CHECK_URL,mToken,jsonRequest,iSuccessCallback,iFailCallback);
        Log.d(TAG,"X: run");
    }
}
