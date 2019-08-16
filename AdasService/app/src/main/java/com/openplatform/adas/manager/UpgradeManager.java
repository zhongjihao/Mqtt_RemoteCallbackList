package com.openplatform.adas.manager;

import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.DeviceCommand;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.datamodel.MqttResponse;
import com.openplatform.adas.datamodel.UpdateItem;
import com.openplatform.adas.datamodel.UpdateItem.DownloadStatus;
import com.openplatform.adas.threadpool.NameThreadFactory;
import com.openplatform.adas.util.MD5Utils;
import com.openplatform.adas.util.OpenPlatformPrefsKeys;
import com.openplatform.adas.util.ShellUtils;
import com.openplatform.adas.util.ShellUtils.CommandResult;
import com.openplatform.aidl.PutMsgRequest;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/8/7 10:21
 * Description :
 */
public class UpgradeManager {
    private final static String TAG = "UpgradeManager";

    private static final Object sLock = new Object();
    private static UpgradeManager sInstance;      // Protected by sLock.
    private HashMap<String, FutureTask<UpdateItem>> downloadingMap = new HashMap<>();
    private ExecutorService downloadES = Executors.newFixedThreadPool(1,new NameThreadFactory("UpgradeManager"));

    public static UpgradeManager getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new UpgradeManager();
            }
            return sInstance;
        }
    }

    private void execCmd(String cmd){
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "E: execCmd");
        Log.d(TAG, "[CMD][" + cmd + "]");
        ShellUtils.CommandResult res = ShellUtils.execCommand(cmd, true);
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "[CMD][" + (endTime - startTime) + " ms]" + "[" + res.result + "]" + "[" + res.errorMsg + "]" + res.successMsg.toString());
        Log.d(TAG, "X: execCmd");
    }

    public void response(String token,String deviceCode,String productType,String msgType,String msgCode,String msgContent){
        Log.d(TAG,"E: response");
        PutMsgRequest request = new PutMsgRequest(deviceCode,productType,msgType,msgCode,msgContent);
        String jsonRequest = new Gson().toJson(request);
        Log.d(TAG,"response----->jsonRequest: "+jsonRequest);
        Factory.get().getHttpEngine().OnPostRequest(UrlConstant.PUTMSG_URL,token,jsonRequest,null,null);
        Log.d(TAG,"X: response");
    }

    private boolean isDownload(UpdateItem item){
        return downloadingMap.get(item.getFileMd5()) != null;
    }

    public void download(long rowId,UpdateItem item,String topic,MqttResponse mqttResponse) {
        if (!isDownload(item)) {
            Log.d(TAG,"start download------>rowId: "+rowId);
            DownloadFutureTask downloadFutureTask = new DownloadFutureTask(rowId,downloadingMap, item,topic,mqttResponse);
            downloadingMap.put(item.getFileMd5(), downloadFutureTask);
            downloadES.execute(downloadFutureTask);
        }else {
            Log.e(TAG,"Download and upgrade in progress rowId: "+rowId);
        }
    }

    private class DownloadFutureTask extends FutureTask<UpdateItem>{

        final HashMap<String, FutureTask<UpdateItem>> downloadingMap;
        final UpdateItem item;
        final MqttResponse mqttResponse;
        final String topic;
        final long rowId;

        public DownloadFutureTask(long rowId,HashMap<String, FutureTask<UpdateItem>> downloadingMap,UpdateItem item,String topic,MqttResponse mqttResponse) {
            super(new DownloadCallable(rowId,item,topic,mqttResponse));
            this.downloadingMap = downloadingMap;
            this.item = item;
            this.topic = topic;
            this.mqttResponse = mqttResponse;
            this.item.setStatus(DownloadStatus.WAITE);
            this.rowId = rowId;
        }

        @Override
        protected void done() {
            super.done();
            Log.d(TAG,"E: done----->download status: "+item.getStatus()+"   rowId: "+rowId);
            try {
                downloadingMap.remove(item.getFileMd5());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(item.getStatus() != DownloadStatus.FAILED){
                Log.d(TAG,"item.getStatus() != DownloadStatus.FAILED");
                final String token = Factory.get().getApplicationPrefs().getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN_DEFAULT);
                final String deviceCode = Factory.get().getApplicationPrefs().getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                final String productType = Factory.get().getApplicationPrefs().getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                Log.d(TAG,"deviceCode: "+deviceCode+"  productType: "+productType+"   download Size: "+item.getProgress()+"  fileSize: "+item.getFileSize());
                if(item.getProgress() >= item.getFileSize()){
                    Log.d(TAG,"download path : "+item.getDownloadUrl());
                    String fileName = MD5Utils.md5sum(item.getDownloadUrl().getBytes());
                    File DOWNLOAD_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File downloadFile = new File(DOWNLOAD_DIRECTORY, fileName);
                    item.setStatus(DownloadStatus.CHECK);
                    String md5 = MD5Utils.md5sum(downloadFile.getAbsolutePath());
                    Log.d(TAG,"download fileMd5:"+md5+", remote fileMd5: "+item.getFileMd5());
                    if(md5!=null && md5.equalsIgnoreCase(item.getFileMd5())){
                        Log.d(TAG,"download success");
                        response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.DOWNLOAD_SUCCESS,"下载完成");

                        ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本下载完成");
                        mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_SUCCESS);
                        NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);

                        item.setStatus(DownloadStatus.INSTALL);

                        Log.d(TAG,"getAbsolutePath:" + downloadFile.getAbsolutePath());
                        CommandResult result = ShellUtils.execCommand("pm install -r " + downloadFile.getAbsolutePath() + "\n", true);
                        if (result != null && result.successMsg != null) {
                            for(int i=0;i<result.successMsg.size();i++){
                                Log.d(TAG,"exec result index: "+i+"   value: "+result.successMsg.get(i));
                            }
                        }
                        if (result != null && result.successMsg != null && (0< result.successMsg.size()) && result.successMsg.get(result.successMsg.size() - 1).toLowerCase().contains("SUCCESS".toLowerCase())) {
                            Log.d(TAG,"Upgrade COMPLETE");
                            Factory.get().getDbManager().updateCmdState(rowId,DeviceCommand.MqttUpgradeCmdState.UPGRADE_SUCCESS);
                            item.setStatus(DownloadStatus.COMPLETE);
                            Factory.get().getApplicationPrefs().putLong(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAINAPP_VERSION,item.getVersion());
                            response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.UPGRADE_SUCCESS,item.getVersion()+"版本升级成功");

                            ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本升级成功");
                            mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.UPGRADE_SUCCESS);
                            NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);

                            downloadFile.delete();

                            SystemClock.sleep(1500);
                        }else {
                            Log.e(TAG,"Upgrade Failed  download path: "+downloadFile.getAbsolutePath());
                            Factory.get().getDbManager().updateUpgradeFile(rowId,DeviceCommand.MqttUpgradeCmdState.UPGRADE_FAILED,downloadFile.getAbsolutePath());
                            response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.UPGRADE_FAILED,item.getVersion()+"版本升级失败");

                            ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本升级失败");
                            mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.UPGRADE_FAILED);
                            NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                        }

                        execCmd("reboot");
                    }else {
                        Log.d(TAG,"download Failed");

                        Factory.get().getDbManager().updateCmdState(rowId,DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_FAILED);
                        downloadFile.delete();
                        item.setProgress(0);
                        item.setStatus(DownloadStatus.FAILED);
                        response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.DOWNLOAD_FAILED,item.getVersion()+"版本下载失败");

                        ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本下载失败");
                        mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_FAILED);
                        NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                    }
                }else{
                    Log.e(TAG,"Download not completed....");

                    Factory.get().getDbManager().updateCmdState(rowId,DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_NOT_COMPLETED);
                    item.setStatus(DownloadStatus.NONE);
                    response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.DOWNLOAD_NOT_COMPLETED,item.getVersion()+"版本下载未完成");

                    ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本下载未完成");
                    mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_NOT_COMPLETED);
                    NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                }
            }else {
                Factory.get().getDbManager().updateCmdState(rowId,DeviceCommand.MqttUpgradeCmdState.ERROR);
            }
            Log.d(TAG,"X: done----->download status: "+item.getStatus());
        }
    }


    private class DownloadCallable implements Callable<UpdateItem> {
        final UpdateItem item;
        final String topic;
        final MqttResponse mqttResponse;
        final long rowId;

        public DownloadCallable(long rowId,UpdateItem item,String topic,MqttResponse mqttResponse) {
            this.item = item;
            this.topic = topic;
            this.mqttResponse = mqttResponse;
            this.rowId = rowId;
        }

        @Override
        public UpdateItem call() throws Exception {
            String downloadUrl = item.getDownloadUrl();
            Log.d(TAG,"E: call------>rowId: "+rowId+"   downloadUrl: "+downloadUrl);
            if(TextUtils.isEmpty(downloadUrl)){
                return item;
            }
            String fileName = MD5Utils.md5sum(downloadUrl.getBytes());
            File DOWNLOAD_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if(!DOWNLOAD_DIRECTORY.exists()) DOWNLOAD_DIRECTORY.mkdirs();
            File downloadFile = new File(DOWNLOAD_DIRECTORY, fileName);
            Log.d(TAG,"call----->path: "+downloadFile.getAbsolutePath());
            long startPos = downloadFile.length();
            long endPos = item.getFileSize();
            Log.d(TAG,"call------>startPos: "+startPos+"    endPos:"+endPos+"   rowId: "+rowId);
            item.setProgress(startPos);
            item.setStatus(DownloadStatus.PAUSE);
            InputStream inStream = null;
            RandomAccessFile randomFile = null;
            final String token = Factory.get().getApplicationPrefs().getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN_DEFAULT);
            final String deviceCode = Factory.get().getApplicationPrefs().getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
            final String productType = Factory.get().getApplicationPrefs().getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
            Log.d(TAG,"deviceCode: "+deviceCode+"  productType: "+productType);
            try {
                URL downUrl = new URL(downloadUrl);
                HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
                http.setConnectTimeout(10 * 1000);
                http.setRequestMethod("GET");
                http.setRequestProperty(
                        "Accept",
                        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                http.setRequestProperty("Accept-Language", "zh-CN");
                http.setRequestProperty("Referer", downUrl.toString());
                http.setRequestProperty("Charset", "UTF-8");

                http.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                http.setRequestProperty(
                        "User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                http.setRequestProperty("Connection", "Keep-Alive");

                int responseCode = http.getResponseCode();
                Log.d(TAG,"call------>responseCode: "+responseCode);
                if(responseCode >= 400){
                    Factory.get().getDbManager().updateCmdState(rowId,DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_FAILED);
                    item.setStatus(DownloadStatus.FAILED);
                    response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.DOWNLOAD_FAILED,item.getVersion()+"版本下载失败");

                    ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本下载失败");
                    mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_FAILED);
                    NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                }else{
                    inStream = http.getInputStream();
                    byte[] buffer = new byte[1024];
                    long readSize = 0;

                    randomFile = new RandomAccessFile(downloadFile, "rwd");
                    randomFile.seek(startPos);
                    Log.d(TAG,"call------->200 start write data to file  startPos: "+startPos+"   endPos: "+endPos);
                    long downloaded = startPos;
                    while ((readSize = inStream.read(buffer, 0, 1024)) != -1 && !Thread.currentThread().isInterrupted() && startPos<endPos) {
                        randomFile.write(buffer, 0, (int) readSize);
                        downloaded += readSize;
                        item.setProgress(downloaded);
                    }
                    Log.d(TAG,"call-------> write data to file done  downloaded: "+downloaded+"    endPos: "+endPos+"  progress: "+item.getProgress());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(randomFile != null){
                    randomFile.close();
                }

                if(inStream != null){
                    inStream.close();
                }
            }
            Log.d(TAG,"X: call");
            return item;
        }
    }



}
