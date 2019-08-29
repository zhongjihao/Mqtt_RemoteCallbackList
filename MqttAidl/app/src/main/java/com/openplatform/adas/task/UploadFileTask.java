package com.openplatform.adas.task;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/5/28 10:21
 * Description :
 */
public class UploadFileTask implements Runnable {
    private static final String TAG = "UploadFileTask";
    private static final int SELFCHECK_THREAD_PRIORITY = Process.THREAD_PRIORITY_URGENT_AUDIO;

    private ISuccessCallback iSuccessCallback;
    private IFailCallback iFailCallback;
    private String filePath;
    private String batchNum;
    private String mToken;

    public UploadFileTask(ISuccessCallback iSuccessCallback,IFailCallback iFailCallback,String token, String filePath) {
        this.iSuccessCallback = iSuccessCallback;
        this.iFailCallback = iFailCallback;
        this.mToken = token;
        this.filePath = filePath;
    }

    public UploadFileTask(ISuccessCallback iSuccessCallback,IFailCallback iFailCallback,String token, String batchNum,String filePath) {
        this.iSuccessCallback = iSuccessCallback;
        this.iFailCallback = iFailCallback;
        this.mToken = token;
        this.batchNum = batchNum;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        Process.setThreadPriority(SELFCHECK_THREAD_PRIORITY);
        Log.d(TAG,"E: run------>filePath: "+filePath+"   batchNum: "+batchNum);
        if(!TextUtils.isEmpty(batchNum)){
            Factory.get().getHttpEngine().OnUploadFile(UrlConstant.BATCH_UPLOAD_FILE_URL,mToken,batchNum,filePath,iSuccessCallback,iFailCallback);
        }else {
            Factory.get().getHttpEngine().OnUploadFile(UrlConstant.UPLOAD_FILE_URL,mToken,filePath,iSuccessCallback,iFailCallback);
        }

        Log.d(TAG,"X: run");
    }
}
