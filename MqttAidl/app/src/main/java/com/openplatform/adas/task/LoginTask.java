package com.openplatform.adas.task;

import android.os.Process;
import android.util.Log;


import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;

import com.google.gson.Gson;
import com.openplatform.aidl.LoginRequest;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/17 10:21
 * Description :
 */
public class LoginTask implements Runnable {
    private static final String TAG = "LoginTask";
    private static final int LOGIN_THREAD_PRIORITY = Process.THREAD_PRIORITY_URGENT_AUDIO;

    private ISuccessCallback iSuccessCallback;
    private IFailCallback iFailCallback;
    private LoginRequest mLoginReq;

    public LoginTask(ISuccessCallback iSuccessCallback,IFailCallback iFailCallback,LoginRequest loginRequest) {
        this.iSuccessCallback = iSuccessCallback;
        this.iFailCallback = iFailCallback;
        this.mLoginReq = loginRequest;
    }

    @Override
    public void run() {
        Process.setThreadPriority(LOGIN_THREAD_PRIORITY);
        String jsonRequest = new Gson().toJson(mLoginReq);
        Log.d(TAG,"E: run------>jsonRequest: "+jsonRequest);
        Factory.get().getHttpEngine().OnPostRequest(UrlConstant.LOGIN_URL,null,jsonRequest,iSuccessCallback,iFailCallback);
        Log.d(TAG,"X: run");
    }
}
