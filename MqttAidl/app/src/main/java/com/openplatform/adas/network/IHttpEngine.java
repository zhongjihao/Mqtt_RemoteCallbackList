package com.openplatform.adas.network;

import java.util.HashMap;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/16 10:21
 * Description :
 */
public interface IHttpEngine {
    String KEY_BODY = "body";

    void OnPostRequest(String url, String token,String jsonRequest, ISuccessCallback iSuccessCallback, IFailCallback iFailCallback);
    void OnGetRequest(String url,String token,String jsonRequest,ISuccessCallback iSuccessCallback,IFailCallback iFailCallback);
    void OnUploadFile(String url, String token,String filePath, ISuccessCallback iSuccessCallback, IFailCallback iFailCallback);
    void OnUploadFile(String url, String token,String batchNum,String filePath, ISuccessCallback iSuccessCallback, IFailCallback iFailCallback);

    interface ISuccessCallback {
        void onSuccess(HashMap<String, String> result);
    }

    interface IFailCallback {
        void onFail(int errorCode,String errorStr);
    }
}
