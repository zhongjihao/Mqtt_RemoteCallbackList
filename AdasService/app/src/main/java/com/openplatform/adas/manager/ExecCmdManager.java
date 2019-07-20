package com.openplatform.adas.manager;

import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.DeviceCommand;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.datamodel.MqttResponse;
import com.openplatform.adas.datamodel.UpdateItem;
import com.openplatform.adas.datamodel.UpdateItem.DownloadStatus;
import com.openplatform.adas.network.IHttpEngine;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;
import com.google.gson.Gson;
import com.openplatform.adas.threadpool.NameThreadFactory;
import com.openplatform.adas.util.AdasPrefs;
import com.openplatform.adas.util.Assert;
import com.openplatform.adas.util.FileUtil;
import com.openplatform.adas.util.FileUtil.Storage;
import com.openplatform.adas.util.MD5Utils;
import com.openplatform.adas.util.OpenPlatformPrefsKeys;
import com.openplatform.adas.util.ShellUtils;
import com.openplatform.adas.util.ShellUtils.CommandResult;
import com.openplatform.aidl.DownUpgradeInfo;
import com.openplatform.aidl.PutMsgRequest;
import com.openplatform.aidl.ServerParamDownloadResponse;
import com.openplatform.aidl.TerminalParamDownloadResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/29 10:21
 * Description :
 */
public class ExecCmdManager {
    private static final String TAG = "ExecCmdManager";
    private AtomicBoolean mExecStatus;
    private String mCmd;
    private AdasPrefs prefs;
    private HashMap<String, FutureTask<UpdateItem>> downloadingMap = new HashMap<>();
    private ExecutorService downloadES = Executors.newFixedThreadPool(1,new NameThreadFactory("downloadApp"));

    public ExecCmdManager(String mqttMsg){
        Log.d(TAG,"E: ExecCmdManager---->mqttMsg: "+mqttMsg);
        Assert.isTrue(mqttMsg!=null && mqttMsg.length()>0);
        prefs = Factory.get().getApplicationPrefs();
        mCmd = mqttMsg;
        Log.d(TAG,"X: ExecCmdManager");
    }

    public void processEvent(AtomicBoolean execStatus){
        mExecStatus = execStatus;
        mExecStatus.set(false);
        try {
            JSONObject jsonCmdMsg = new JSONObject(mCmd);
            String deviceId = jsonCmdMsg.getString("deviceId");
            String cmdSNO = jsonCmdMsg.getString("cmdSNO");
            final String command = jsonCmdMsg.getString("command");
            Log.d(TAG,"processEvent------>deviceId: "+deviceId+"  cmdSNO: "+cmdSNO+"  command: "+command);

            if(command.equalsIgnoreCase("takePicture")){
                String[] cameraIds = null;
                String data = "拍照指令data数据错误";
                try {
                    data = jsonCmdMsg.getString("data");
                    Log.d(TAG,"processEvent---1--takePic cmd---->data: "+data);
                    JSONObject jsonObject = new JSONObject(data);
                    String channelData = jsonObject.getString("cameraId");
                    Log.d(TAG,"processEvent---2---takePic cmd---->channelData: "+channelData);

                    JSONArray array = jsonObject.getJSONArray("cameraId");
                    cameraIds = new String[array.length()];
                    for(int i = 0;i<array.length();i++){
                        cameraIds[i] = array.getString(i);
                    }
                    int channelNum = cameraIds.length;
                    Log.d(TAG,"processEvent--3---takePic cmd---->channelNum: "+channelNum);
                }catch(Exception e){
                    e.printStackTrace();
                    MqttResponse mqttResponse = new MqttResponse();
                    mqttResponse.setDeviceId(deviceId);
                    mqttResponse.setCmdSNO(cmdSNO);
                    mqttResponse.setCommand(command);
                    MqttResponse.Response takePictureResponse = new MqttResponse.Response();
                    takePictureResponse.setFlag(true);
                    takePictureResponse.setMessage(data);
                    takePictureResponse.setData("");
                    mqttResponse.setResponse(takePictureResponse);
                    mqttResponse.setState(DeviceCommand.MqttTakePicCmd.DateError);
                    NotifyManager.getInstance().OnMqttTakePicNotify(command,mqttResponse,null);
                    return;
                }
                Log.d(TAG,"procMqttCmd-----takePic cmd");
                if(cameraIds != null){
                    MqttResponse mqttResponse = new MqttResponse();
                    mqttResponse.setDeviceId(deviceId);
                    mqttResponse.setCmdSNO(cmdSNO);
                    mqttResponse.setCommand(command);
                    MqttResponse.Response takePictureResponse = new MqttResponse.Response();
                    takePictureResponse.setFlag(true);
                    takePictureResponse.setMessage("收到拍照指令");
                    takePictureResponse.setData("");
                    mqttResponse.setResponse(takePictureResponse);
                    mqttResponse.setState(DeviceCommand.MqttTakePicCmd.CmdReceived);

                    NotifyManager.getInstance().OnMqttTakePicNotify(command,mqttResponse,cameraIds);
                }
            }else if(command.equalsIgnoreCase("reboot")){
                Log.d(TAG,"processEvent-----reboot cmd");
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到reboot指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                SystemClock.sleep(1500);
                execCmd("reboot");
            }else if(command.equalsIgnoreCase("formatsdcard0")){
                Log.d(TAG,"processEvent-----format sdcard0 cmd");
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到格式化内置SD卡指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                FileUtil.formatSdcard(Factory.get().getApplicationContext(), Storage.INTERNAL);
            }else if(command.equalsIgnoreCase("formatsdcard1")){
                Log.d(TAG,"processEvent-----format sdcard1 cmd");
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到格式化外置SD卡指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                FileUtil.formatSdcard(Factory.get().getApplicationContext(), Storage.EXTERNAL);
            }else if(command.equalsIgnoreCase("clearCache")){
                Log.d(TAG,"processEvent-----clear cache cmd");
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到清缓存指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                SystemClock.sleep(1500);
                execCmd("pm clear com.ad.adas");
                execCmd("reboot");
            }else if(command.equalsIgnoreCase("customCmd")){
                String cmd = jsonCmdMsg.getString("data");
                Log.d(TAG,"processEvent-----custom cmd : "+cmd);
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到自定义指令: "+cmd);
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                execCmd(cmd);
            }else if(command.equalsIgnoreCase("TerminalParam")){
                Log.d(TAG,"processEvent-----Terminal Param cmd");
                final MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到终端参数指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttTermParamCmd.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);

                final String token = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN_DEFAULT);
                final String deviceCode = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                final String deviceVersion = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION_DEFAULT);
                final String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                Log.d(TAG, "processEvent---Terminal param--->deviceCode: " + deviceCode + "  deviceVersion: " + deviceVersion + "  productType: " + productType);
                try {
                    JSONObject object = new JSONObject();
                    object.put("deviceCode", deviceCode);
                    object.put("deviceVersion", deviceVersion);
                    object.put("platType", "0");
                    object.put("funcType", "0");
                    Factory.get().getHttpEngine().OnPostRequest(UrlConstant.DEVICE_PARAM_DOWNLOAD_URL, token, object.toString(), new ISuccessCallback() {
                        @Override
                        public void onSuccess(HashMap<String, String> result) {
                            String body = result.get(IHttpEngine.KEY_BODY);
                            Log.d(TAG, "Terminal Param Download onSuccess---->body: " + body);
                            try {
                                TerminalParamDownloadResponse response = new Gson().fromJson(body, TerminalParamDownloadResponse.class);
                                Log.d(TAG, "Terminal Param Download onSuccess---->response: " + response.toString());
                                response(token, deviceCode, productType, DeviceCommand.TermParam.CODE, DeviceCommand.TermParam.SUCCESS, "获取终端参数成功");
                                NotifyManager.getInstance().OnTerminalParamNotify(response);

                                mqttResponse.setState(DeviceCommand.MqttTermParamCmd.SUCCESS);
                                ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage("获取终端参数成功");
                                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mqttResponse.setState(DeviceCommand.MqttTermParamCmd.FAILED);
                                ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage("获取终端参数失败");
                                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                            }
                        }
                    }, new IFailCallback() {
                        @Override
                        public void onFail(int errorCode, String errorStr) {
                            Log.e(TAG, "Terminal Param Download  errorCode: " + errorCode + "   errorStr: " + errorStr);
                            response(token, deviceCode, productType, DeviceCommand.TermParam.CODE, DeviceCommand.TermParam.FAILED, "获取终端参数失败");

                            mqttResponse.setState(DeviceCommand.MqttTermParamCmd.FAILED);
                            ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage("获取终端参数失败");
                            NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(command.equalsIgnoreCase("ServerParam")){
                Log.d(TAG,"processEvent-----Server Param cmd");
                final MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到终端服务器参数指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttServerParamCmd.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);

                final String token = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN_DEFAULT);
                final String deviceCode = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                final String deviceVersion = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION_DEFAULT);
                final String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                Log.d(TAG, "processEvent---Server Param--->deviceCode: " + deviceCode + "  deviceVersion: " + deviceVersion + "  productType: " + productType);
                try {
                    JSONObject object = new JSONObject();
                    object.put("deviceCode", deviceCode);
                    object.put("deviceVersion", deviceVersion);
                    Factory.get().getHttpEngine().OnPostRequest(UrlConstant.SERVER_PARAM_DOWNLOAD_URL, token, object.toString(), new ISuccessCallback() {
                        @Override
                        public void onSuccess(HashMap<String, String> result) {
                            String body = result.get(IHttpEngine.KEY_BODY);
                            Log.d(TAG, "Server Param Download onSuccess---->body: " + body);
                            try {
                                ServerParamDownloadResponse response = new Gson().fromJson(body, ServerParamDownloadResponse.class);
                                Log.d(TAG, "Server Param Download onSuccess---->response: " + response.toString());
                                response(token, deviceCode, productType, DeviceCommand.ServerParam.CODE, DeviceCommand.ServerParam.SUCCESS, "获取服务器参数成功");
                                NotifyManager.getInstance().OnServerParamNotify(response);

                                mqttResponse.setState(DeviceCommand.MqttServerParamCmd.SUCCESS);
                                ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage("获取服务器参数成功");
                                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mqttResponse.setState(DeviceCommand.MqttServerParamCmd.FAILED);
                                ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage("获取服务器参数失败");
                                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                            }
                        }
                    }, new IFailCallback() {
                        @Override
                        public void onFail(int errorCode, String errorStr) {
                            Log.e(TAG, "Server Param Download  errorCode: " + errorCode + "   errorStr: " + errorStr);
                            response(token, deviceCode, productType, DeviceCommand.ServerParam.CODE, DeviceCommand.ServerParam.FAILED, "获取服务器参数失败");
                            mqttResponse.setState(DeviceCommand.MqttServerParamCmd.FAILED);
                            ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage("获取服务器参数失败");
                            NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(command.equalsIgnoreCase("upgradeCmd")){
                Log.d(TAG,"processEvent-----upgrade Cmd");
                final MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到升级指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.CmdReceived);

                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);

                final String token = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN_DEFAULT);
                final String deviceCode = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                final String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                Log.d(TAG, "processEvent---upgrade---->deviceCode: " + deviceCode + "  productType: " + productType);
                try {
                    JSONObject object = new JSONObject();
                    object.put("deviceCode", deviceCode);
                    object.put("productType", productType);
                    Factory.get().getHttpEngine().OnPostRequest(UrlConstant.DOWN_UPGRADEINFO_URL, token, object.toString(),
                            new ISuccessCallback() {
                                @Override
                                public void onSuccess(HashMap<String, String> result) {
                                    String body = result.get(IHttpEngine.KEY_BODY);
                                    Log.d(TAG, "OnDownUpgradeInfo onSuccess---->body: " + body);
                                    try {
                                        DownUpgradeInfo response = new Gson().fromJson(body, DownUpgradeInfo.class);
                                        Log.d(TAG, "OnDownUpgradeInfo onSuccess---->response: " + response.toString());
                                        if (response.getData() != null && response.getData().length > 0) {
                                            for (DownUpgradeInfo.Data upgrade : response.getData()) {
                                                if (upgrade.getApkType().equalsIgnoreCase(DeviceCommand.Upgrade.UpgradeApp.MAIN_APP)) {//主应用Adas
                                                    long versionCode = prefs.getLong(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAINAPP_VERSION, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAINAPP_VERSION_DEFAULT);
                                                    Log.d(TAG, "OnDownUpgradeInfo-----upgrade main app, remote version: " + upgrade.getDeviceVersion() + "  local version: " + versionCode);
                                                    if(versionCode != upgrade.getDeviceVersion()){
                                                        UpdateItem item = new UpdateItem();
                                                        item.setDownloadUrl(upgrade.getFileFullName());
                                                        item.setFileSize(upgrade.getFileSize());
                                                        item.setFileMd5(upgrade.getFileMd5());
                                                        item.setVersion(upgrade.getDeviceVersion());
                                                        download(item,command,mqttResponse);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new IFailCallback() {
                                @Override
                                public void onFail(int errorCode, String errorStr) {
                                    Log.e(TAG, "OnDownUpgradeInfo  errorCode: " + errorCode + "   errorStr: " + errorStr);
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(command.equalsIgnoreCase("deviceDetect")){
                Log.d(TAG,"processEvent-----device Detect cmd");
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到自检指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);
                NotifyManager.getInstance().OnMqttSimpleCmdNotify(command,mqttResponse);
            }else if(command.equalsIgnoreCase("heightAdjust")){
                Log.d(TAG,"processEvent-----height Adjust cmd");
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到等高校准指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);
                NotifyManager.getInstance().OnMqttTakePicNotify(command,mqttResponse,null);
            }else if(command.equalsIgnoreCase("ldwAdjust")){
                Log.d(TAG,"processEvent-----height Adjust cmd");
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response response = new MqttResponse.Response();
                response.setFlag(true);
                response.setMessage("收到车道校准指令");
                response.setData("");
                mqttResponse.setResponse(response);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdReceived);
                NotifyManager.getInstance().OnMqttTakePicNotify(command,mqttResponse,null);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.d(TAG,"X: processEvent");
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

    private void response(String token,String deviceCode,String productType,String msgType,String msgCode,String msgContent){
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

    private void download(UpdateItem item,String topic,MqttResponse mqttResponse) {
        if (!isDownload(item)) {
            Log.d(TAG,"start download");
            DownloadFutureTask downloadFutureTask = new DownloadFutureTask(downloadingMap, item,topic,mqttResponse);
            downloadingMap.put(item.getFileMd5(), downloadFutureTask);
            downloadES.execute(downloadFutureTask);
        }else {
            Log.e(TAG,"Download and upgrade in progress ....");
        }
    }

    private class DownloadFutureTask extends FutureTask<UpdateItem>{

        final HashMap<String, FutureTask<UpdateItem>> downloadingMap;
        final UpdateItem item;
        final MqttResponse mqttResponse;
        final String topic;

        public DownloadFutureTask(HashMap<String, FutureTask<UpdateItem>> downloadingMap,UpdateItem item,String topic,MqttResponse mqttResponse) {
            super(new DownloadCallable(item,topic,mqttResponse));
            this.downloadingMap = downloadingMap;
            this.item = item;
            this.topic = topic;
            this.mqttResponse = mqttResponse;
            item.setStatus(DownloadStatus.WAITE);
        }

        @Override
        protected void done() {
            super.done();
            try {
                downloadingMap.remove(item.getFileMd5());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG,"E: done----->download status: "+item.getStatus());
            if(item.getStatus() != DownloadStatus.FAILED){
                Log.d(TAG,"item.getStatus() != DownloadStatus.FAILED");
                final String token = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN_DEFAULT);
                final String deviceCode = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                final String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
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
                            item.setStatus(DownloadStatus.COMPLETE);
                            prefs.putLong(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAINAPP_VERSION,item.getVersion());
                            response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.UPGRADE_SUCCESS,item.getVersion()+"版本升级成功");

                            ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本升级成功");
                            mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.UPGRADE_SUCCESS);
                            NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                        }else {
                            Log.e(TAG,"Upgrade Failed");
                            response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.UPGRADE_FAILED,item.getVersion()+"版本升级失败");

                            ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本升级失败");
                            mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.UPGRADE_FAILED);
                            NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                        }
                        downloadFile.delete();

                        SystemClock.sleep(1500);
                        execCmd("reboot");
                    }else {
                        Log.d(TAG,"download Failed");
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
                    item.setStatus(DownloadStatus.NONE);
                    response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.DOWNLOAD_NOT_COMPLETED,item.getVersion()+"版本下载未完成");

                    ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本下载未完成");
                    mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_NOT_COMPLETED);
                    NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                }
            }
            Log.d(TAG,"X: done----->download status: "+item.getStatus());
        }
    }

    private class DownloadCallable implements Callable<UpdateItem> {
        final UpdateItem item;
        final String topic;
        final MqttResponse mqttResponse;

        public DownloadCallable(UpdateItem item,String topic,MqttResponse mqttResponse) {
            this.item = item;
            this.topic = topic;
            this.mqttResponse = mqttResponse;
        }

        @Override
        public UpdateItem call() throws Exception {
            String downloadUrl = item.getDownloadUrl();
            Log.d(TAG,"E: call------>downloadUrl: "+downloadUrl);
            if(TextUtils.isEmpty(downloadUrl)) return item;
            String fileName = MD5Utils.md5sum(downloadUrl.getBytes());
            File DOWNLOAD_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if(!DOWNLOAD_DIRECTORY.exists()) DOWNLOAD_DIRECTORY.mkdirs();
            File downloadFile = new File(DOWNLOAD_DIRECTORY, fileName);
            Log.d(TAG,"call----->path: "+downloadFile.getAbsolutePath());
            long startPos = downloadFile.length();
            long endPos = item.getFileSize();
            Log.d(TAG,"call------>startPos: "+startPos+"    endPos:"+endPos);
            item.setProgress(startPos);
            item.setStatus(DownloadStatus.PAUSE);
            InputStream inStream = null;
            RandomAccessFile randomFile = null;
            final String token = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN_DEFAULT);
            final String deviceCode = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
            final String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
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
                    item.setStatus(DownloadStatus.FAILED);
                    response(token,deviceCode,productType,DeviceCommand.Upgrade.CODE,DeviceCommand.Upgrade.DOWNLOAD_FAILED,item.getVersion()+"版本下载失败");

                    ((MqttResponse.Response)(mqttResponse.getResponse())).setMessage(item.getVersion()+"版本下载失败");
                    mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.DOWNLOAD_FAILED);
                    NotifyManager.getInstance().OnMqttSimpleCmdNotify(topic,mqttResponse);
                }else{
                    Log.d(TAG,"call------->200 start write data to file");
                    inStream = http.getInputStream();
                    byte[] buffer = new byte[1024];
                    long readSize = 0;

                    randomFile = new RandomAccessFile(downloadFile, "rwd");
                    randomFile.seek(startPos);

                    long downloaded = startPos;
                    while ((readSize = inStream.read(buffer, 0, 1024)) != -1 && !Thread.currentThread().isInterrupted() && startPos<endPos) {
                        randomFile.write(buffer, 0, (int) readSize);
                        downloaded += readSize;
                        item.setProgress(downloaded);
                    }
                    Log.d(TAG,"call-------> write data to file done !");
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