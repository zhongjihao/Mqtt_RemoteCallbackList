package com.openplatform.adas;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.openplatform.adas.constant.DeviceCommand;
import com.openplatform.adas.constant.UrlConstant;
import com.openplatform.adas.datamodel.CameraState;
import com.openplatform.adas.datamodel.DeviceUploadFileResponse;
import com.openplatform.adas.datamodel.MqttParamResponse;
import com.openplatform.adas.datamodel.MqttResponse;
import com.openplatform.adas.datamodel.PutUpFileInfo;
import com.openplatform.adas.network.IHttpEngine;
import com.openplatform.adas.network.IHttpEngine.ISuccessCallback;
import com.openplatform.adas.network.IHttpEngine.IFailCallback;
import com.google.gson.Gson;
import com.openplatform.adas.datamodel.BaseResponse;
import com.openplatform.adas.datamodel.LoginResponse;
import com.openplatform.adas.interfacemanager.INotifyCallback;
import com.openplatform.adas.manager.NotifyManager;
import com.openplatform.adas.receiver.SMSBroadcastReceiver;
import com.openplatform.adas.service.MQTTService;
import com.openplatform.adas.task.CheckSimByIccidTask;
import com.openplatform.adas.task.LoginTask;
import com.openplatform.adas.task.PutUpFileInfoTask;
import com.openplatform.adas.task.SelfCheckTask;
import com.openplatform.adas.task.ServerParamDownloadTask;
import com.openplatform.adas.task.SyncTimeTask;
import com.openplatform.adas.task.TerminalParamDownloadTask;
import com.openplatform.adas.task.UploadFileTask;
import com.openplatform.adas.threadpool.NameThreadFactory;
import com.openplatform.adas.util.AdasPrefs;
import com.openplatform.adas.util.NetUtil;
import com.openplatform.adas.util.OpenPlatformPrefsKeys;
import com.openplatform.aidl.CmdMesage;
import com.openplatform.aidl.IAdasBinder;
import com.openplatform.aidl.IAdasCallback;
import com.openplatform.aidl.LoginRequest;
import com.openplatform.aidl.PutMsgRequest;
import com.openplatform.aidl.SelfCheck;
import com.openplatform.aidl.ServerParamDownloadResponse;
import com.openplatform.aidl.TerminalParamDownloadResponse;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/17 10:21
 * Description :
 */
public class AdasService extends Service {
    private final static String TAG = "AdasService";
    private final static int MSG_NETWORK = 1;
    private final static int MSG_DOWNLOAD_SERVER_PARAM = 2;
    private final static int MSG_DOWNLOAD_TERMINAL_PARAM = 3;
    private final static int MSG_LOGIN_CONTINUE = 4;
    private RemoteCallbackList<IAdasCallback> mRemoteCallbackList = new RemoteCallbackList<>();
    private ExecutorService mSyncEs = Executors.newFixedThreadPool(1, new NameThreadFactory("openPlatformTask"));
    private ExecutorService fileInfoUploadEs = Executors.newFixedThreadPool(1, new NameThreadFactory("fileInfoUploadEs"));
    private String mToken;
    private Context mContext;
    private INotifyCallback notifyCallback;
    private ILoginNotify loginNotify;
    private MQTTService mqttService;
    private CameraState dsmCameraState;
    private CameraState adasCameraState;
    private CameraState h264CameraState;
    private int countTakePicTotal = 0;
    private int takePicDoneNum = 0;
    private PutUpFileInfo.Data[] mFileList;
    private PutUpFileInfo mPutUpFileInfo;


    public interface ILoginNotify{
        public void OnLogin();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mContext = this;
        notifyCallback = new INotifyCallback() {
            @Override
            public void terminalParamCallback(TerminalParamDownloadResponse response) {
                try {
                    final int len = mRemoteCallbackList.beginBroadcast();
                    Log.d(TAG,"Terminal Param Download onSuccess---->callback num: "+len+"   token: "+mToken);
                    for (int i = 0; i < len; i++) {
                        try {
                            // 通知回调
                            mRemoteCallbackList.getBroadcastItem(i).terminalParamCallback(response);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    mRemoteCallbackList.finishBroadcast();
                    Log.d(TAG,"Terminal Param Download onSuccess------>finishBroadcast");
                }
            }

            @Override
            public void serverParamCallback(ServerParamDownloadResponse response) {
                try {
                    final int len = mRemoteCallbackList.beginBroadcast();
                    Log.d(TAG,"Server Param Download onSuccess---->callback num: "+len+"   token: "+mToken);
                    for (int i = 0; i < len; i++) {
                        try {
                            // 通知回调
                            mRemoteCallbackList.getBroadcastItem(i).serverParamCallback(response);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    mRemoteCallbackList.finishBroadcast();
                    Log.d(TAG,"Server Param Download onSuccess------>finishBroadcast");
                }
            }

            @Override
            public void mqttTakePicEvent(String topic,MqttResponse mqttResponse,String[] cameraIds){
                Log.d(TAG,"mqttTakePicEvent");
                String jsonResponse = new Gson().toJson(mqttResponse);
                Log.d(TAG,"mqttTakePicEvent----recv takePic cmd------>jsonRequest: "+jsonResponse);
                mqttService.publish(topic,jsonResponse,0);

                if(cameraIds != null){//拍照指令
                    try {
                        countTakePicTotal = 0;
                        takePicDoneNum = 0;
                        final int len = mRemoteCallbackList.beginBroadcast();
                        Log.d(TAG,"mqttTakePicEvent-----beginBroadcast------>len: "+len);
                        for (int i = 0; i < len; i++) {
                            try {
                                // 通知回调
                                int channelNum = cameraIds.length;
                                for(int index = 0;index<channelNum; index++){
                                    if(Integer.valueOf(cameraIds[index]) == 100){
                                        if(adasCameraState.isMount() && adasCameraState.isHasFrame()){
                                            countTakePicTotal++;
                                        }
                                    }else if(Integer.valueOf(cameraIds[index]) == 101){
                                        if(dsmCameraState.isMount() && dsmCameraState.isHasFrame()){
                                            countTakePicTotal++;
                                        }
                                    }else if(Integer.valueOf(cameraIds[index]) == 3){
                                        if(h264CameraState.isMount() && h264CameraState.isHasFrame()){
                                            countTakePicTotal++;
                                        }
                                    }
                                }
                                Log.d(TAG,"mqttTakePicEvent-----takePic cmd----->channelNum: "+channelNum+"   countTakePicTotal: "+countTakePicTotal);
                                if(countTakePicTotal > 0){
                                    mPutUpFileInfo = new PutUpFileInfo();
                                    mFileList = new PutUpFileInfo.Data[countTakePicTotal];
                                    for(int index = 0;index<channelNum; index++){
                                        Log.d(TAG,"mqttTakePicEvent-----takePic cmd---->channel: "+cameraIds[index]);
                                        mRemoteCallbackList.getBroadcastItem(i).mqttTakePic(topic,mqttResponse.getDeviceId(),mqttResponse.getCmdSNO(),mqttResponse.getCommand(),Integer.valueOf(cameraIds[index]));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            mRemoteCallbackList.finishBroadcast();
                            Log.d(TAG,"mqttTakePicEvent------takePic cmd------>finishBroadcast");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }else if(mqttResponse.getCommand().equalsIgnoreCase("heightAdjust")){//等高校准
                    try {
                        final int len = mRemoteCallbackList.beginBroadcast();
                        Log.d(TAG,"mqttTakePicEvent---heightAdjust-----beginBroadcast------>len: "+len);
                        for (int i = 0; i < len; i++) {
                            try {
                                // 通知回调
                                mRemoteCallbackList.getBroadcastItem(i).mqttTakePic(topic,mqttResponse.getDeviceId(),mqttResponse.getCmdSNO(),mqttResponse.getCommand(),100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            mRemoteCallbackList.finishBroadcast();
                            Log.d(TAG,"mqttTakePicEvent----heightAdjust-----takePic cmd------>finishBroadcast");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }else if(mqttResponse.getCommand().equalsIgnoreCase("ldwAdjust")){//车道校准
                    try {
                        final int len = mRemoteCallbackList.beginBroadcast();
                        Log.d(TAG,"mqttTakePicEvent---ldwAdjust-----beginBroadcast------>len: "+len);
                        for (int i = 0; i < len; i++) {
                            try {
                                // 通知回调
                                mRemoteCallbackList.getBroadcastItem(i).mqttTakePic(topic,mqttResponse.getDeviceId(),mqttResponse.getCmdSNO(),mqttResponse.getCommand(),100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            mRemoteCallbackList.finishBroadcast();
                            Log.d(TAG,"mqttTakePicEvent----ldwAdjust-----takePic cmd------>finishBroadcast");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void mqttTakePicEvent(String topic,MqttResponse mqttResponse,String batchNum,String channelId,int interval,int count,String distance,String minSpeed,int angle){
                Log.d(TAG,"mqttTakePicEvent----->state: "+mqttResponse.getState());
                String jsonResponse = new Gson().toJson(mqttResponse);
                Log.d(TAG,"mqttTakePicEvent----recv cond takePic cmd------>jsonRequest: "+jsonResponse);
                mqttService.publish(topic,jsonResponse,0);

                if(mqttResponse.getState() == DeviceCommand.MqttTakePicCmd.CmdReceived){
                    try {
                        final int len = mRemoteCallbackList.beginBroadcast();
                        Log.d(TAG,"mqttTakePicEvent---cond--beginBroadcast------>len: "+len);
                        for (int i = 0; i < len; i++) {
                            try {
                                // 通知回调
                                Log.d(TAG, "mqttTakePicEvent--cond takePic cmd---->channel: " + channelId+"  batchNum: "+batchNum+"  interval: "+interval+"  count: "+count);
                                mRemoteCallbackList.getBroadcastItem(i).mqttCondTakePic(topic, mqttResponse.getDeviceId(), mqttResponse.getCmdSNO(), mqttResponse.getCommand(), batchNum, Integer.valueOf(channelId), interval, count, distance, minSpeed, angle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            mRemoteCallbackList.finishBroadcast();
                            Log.d(TAG,"mqttTakePicEvent---cond takePic cmd------>finishBroadcast");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void mqttSimpleCmdEvent(String topic,MqttResponse mqttResponse){
                Log.d(TAG,"mqttSimpleCmdEvent");
                String jsonResponse = new Gson().toJson(mqttResponse);
                Log.d(TAG,"mqttSimpleCmdEvent----recv cmd------>jsonRequest: "+jsonResponse);
                mqttService.publish(topic,jsonResponse,0);

                if(mqttResponse.getCommand().equalsIgnoreCase("deviceDetect")){//自检指令
                    try {
                        final int len = mRemoteCallbackList.beginBroadcast();
                        Log.d(TAG,"mqttSimpleCmdEvent----device detect cmd---->callback num: "+len+"   token: "+mToken);
                        for (int i = 0; i < len; i++) {
                            try {
                                // 通知回调
                                mRemoteCallbackList.getBroadcastItem(i).mqttDeviceDetect(topic,mqttResponse.getDeviceId(),mqttResponse.getCmdSNO(),mqttResponse.getCommand());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            mRemoteCallbackList.finishBroadcast();
                            Log.d(TAG,"mqttSimpleCmdEvent----device detect cmd------>finishBroadcast");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void mqttParamCmdEvent(String topic, MqttParamResponse mqttResponse, List<CmdMesage> list){
                Log.d(TAG,"mqttParamCmdEvent");
                String jsonResponse = new Gson().toJson(mqttResponse);
                Log.d(TAG,"mqttParamCmdEvent----recv cmd------>jsonRequest: "+jsonResponse);
                mqttService.publish(topic,jsonResponse,0);

                if(list != null){
                    if(mqttResponse.getCommand().equalsIgnoreCase("TerminalParamArray")){
                        try {
                            final int len = mRemoteCallbackList.beginBroadcast();
                            Log.d(TAG,"mqttParamCmdEvent----TerminalParam cmd---->callback num: "+len+"   token: "+mToken);
                            for (int i = 0; i < len; i++) {
                                try {
                                    // 通知回调
                                    mRemoteCallbackList.getBroadcastItem(i).mqttParamCmd(topic,mqttResponse.getDeviceId(),mqttResponse.getCmdSNO(),mqttResponse.getCommand(),list);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            try {
                                mRemoteCallbackList.finishBroadcast();
                                Log.d(TAG,"mqttParamCmdEvent----TerminalParam cmd------>finishBroadcast");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }else if(mqttResponse.getCommand().equalsIgnoreCase("TerminalParamArrayUpdate")){
                        try {
                            final int len = mRemoteCallbackList.beginBroadcast();
                            Log.d(TAG,"mqttParamCmdEvent----TerminalParamUpdate cmd---->callback num: "+len+"   token: "+mToken);
                            for (int i = 0; i < len; i++) {
                                try {
                                    // 通知回调
                                    mRemoteCallbackList.getBroadcastItem(i).mqttParamCmd(topic,mqttResponse.getDeviceId(),mqttResponse.getCmdSNO(),mqttResponse.getCommand(),list);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            try {
                                mRemoteCallbackList.finishBroadcast();
                                Log.d(TAG,"mqttParamCmdEvent----TerminalParamUpdate cmd------>finishBroadcast");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void smsParamCmdEvent(String phone,List<CmdMesage> list){
                Log.d(TAG,"smsParamCmdEvent----->phone: "+phone);
                try {
                    final int len = mRemoteCallbackList.beginBroadcast();
                    Log.d(TAG,"smsParamCmdEvent----TerminalParam cmd---->callback num: "+len+"   token: "+mToken);
                    for (int i = 0; i < len; i++) {
                        try {
                            // 通知回调
                            mRemoteCallbackList.getBroadcastItem(i).smsParamCmd(phone,list);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        mRemoteCallbackList.finishBroadcast();
                        Log.d(TAG,"smsParamCmdEvent----TerminalParam cmd------>finishBroadcast");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        loginNotify = new ILoginNotify(){
            @Override
            public void OnLogin(){
                final AdasPrefs prefs = Factory.get().getApplicationPrefs();
                LoginRequest loginRequest = null;

                String mucUnqiueNum =  prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MUCUNQIUE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MUCUNQIUE_DEFAULT);
                Log.d(TAG, "start login----->mucUnqiueNum: "+mucUnqiueNum);
                if(!TextUtils.isEmpty(mucUnqiueNum)){
                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE, mucUnqiueNum);
                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SERIALNO, mucUnqiueNum);
                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO,mucUnqiueNum);
                }

                String simNo = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO_DEFAULT);
                String terminalId = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                String imei = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_IMEI, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_IMEI_DEFAULT);
                String simType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMTYPE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMTYPE_DEFAULT);
                String mac = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAC, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAC_DEFAULT);
                String serialNo = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SERIALNO, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SERIALNO_DEFAULT);
                String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                Log.d(TAG, "start login----->simNo: " + simNo + "  terminalId: " + terminalId + "  imei: " + imei + "  simType: " + simType + "  mac: " + mac + "  serialNo: " + serialNo + "   productType: " + productType);
                loginRequest = new LoginRequest();
                loginRequest.setSimNo(simNo);
                loginRequest.setTerminalId(terminalId);
                loginRequest.setImei(imei);
                loginRequest.setSimType(simType);
                loginRequest.setMacAddress(mac);
                loginRequest.setSerialNo(serialNo);
                loginRequest.setProductType(productType);

                mSyncEs.submit(new LoginTask(new ISuccessCallback() {
                    @Override
                    public void onSuccess(HashMap<String, String> result) {
                        String body = result.get(IHttpEngine.KEY_BODY);
                        Log.d(TAG,"Login onSuccess---->body: "+body);
                        try{
                            LoginResponse loginResponse = new Gson().fromJson(body,LoginResponse.class);
                            mToken = loginResponse.getData().getToken();
                            final AdasPrefs prefs = Factory.get().getApplicationPrefs();
                            prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_TOKEN,mToken);
                            Log.d(TAG,"Login onSuccess---->"+loginResponse.toString());
                            //启动MQTT Server
                            Intent intent = new Intent(mContext, MQTTService.class);
                            mContext.bindService(intent,mqttServerConnection, Context.BIND_AUTO_CREATE);

                            try {
                                final int len = mRemoteCallbackList.beginBroadcast();
                                String simNo = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO_DEFAULT);
                                Log.d(TAG,"Login onSuccess---->callback num: "+len+"   token: "+mToken+"  ,simNo: "+simNo);
                                for (int i = 0; i < len; i++) {
                                    try {
                                        // 通知回调
                                        mRemoteCallbackList.getBroadcastItem(i).loginCallback(mToken,simNo);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                mRemoteCallbackList.finishBroadcast();
                                Log.d(TAG,"Login onSuccess------finishBroadcast");
                            }
                            mHandler.sendEmptyMessage(MSG_DOWNLOAD_SERVER_PARAM);
                            mHandler.sendEmptyMessage(MSG_DOWNLOAD_TERMINAL_PARAM);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new IFailCallback() {
                    @Override
                    public void onFail(int errorCode, String errorStr) {
                        Log.e(TAG,"Login  Failed errorCode: "+errorCode+"   errorStr: "+errorStr);
                        mHandler.sendEmptyMessageDelayed(MSG_LOGIN_CONTINUE, 3000L);
                    }
                }, loginRequest));
            }
        };
        mHandler.sendEmptyMessage(MSG_NETWORK);
        NotifyManager.getInstance().addNotifyListener(notifyCallback);
        adasCameraState = new CameraState();
        dsmCameraState = new CameraState();
        h264CameraState = new CameraState();
    }

    private ServiceConnection mqttServerConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.e(TAG, "onServiceDisconnected:" + arg0.getPackageName());
            mqttService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected:" + name.getPackageName());
            // 获取Service的onBinder方法返回的对象代理
            mqttService = ((MQTTService.MqttBinder)binder).getService();
            mqttService.connectServer(UrlConstant.MQTTSERVER_IP,UrlConstant.MQTTSERVER_PORT);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mHandler.removeCallbacksAndMessages(null);
        NotifyManager.getInstance().removeNotifyListener(notifyCallback);
        unbindService(mqttServerConnection);
        mSyncEs.shutdown();
        fileInfoUploadEs.shutdown();
        mRemoteCallbackList.kill();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_NETWORK:{
                    final AdasPrefs prefs = Factory.get().getApplicationPrefs();
                    String iccid = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_ICCID,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_ICCID_DEFAULT);
                    String mac = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAC,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAC_DEFAULT);
                    String simNo = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO_DEFAULT);
                    boolean isNetOk = NetUtil.isNetworkAvailable(Factory.get().getApplicationContext());
                    String mucUnqiueNum =  prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MUCUNQIUE, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MUCUNQIUE_DEFAULT);
                    Log.d(TAG,"Msg network---->iccid: "+iccid+"   唯一号simNo:"+simNo+"  mac: "+mac+"   isNetOk: "+isNetOk+"   mucUnqiueNum: "+mucUnqiueNum);

                    if(!TextUtils.isEmpty(mucUnqiueNum)){
                        if(isNetOk && !TextUtils.isEmpty(simNo) && (simNo.trim().length() == 11)){
                            loginNotify.OnLogin();
                            mSyncEs.submit(new SyncTimeTask());
                        }else {
                            sendEmptyMessageDelayed(MSG_NETWORK, 3000L);
                        }
                    }else {
                        if(isNetOk && !TextUtils.isEmpty(iccid)){
                            mSyncEs.submit(new CheckSimByIccidTask(iccid,loginNotify));
                            mSyncEs.submit(new SyncTimeTask());
                        } else {
                            sendEmptyMessageDelayed(MSG_NETWORK, 3000L);
                        }
                    }
                    break;
                }
                case MSG_DOWNLOAD_SERVER_PARAM:{
                    final AdasPrefs prefs = Factory.get().getApplicationPrefs();
                    final String terminalId = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                    String apkChannelVersion = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION_DEFAULT);
                    Log.d(TAG,"Msg download server param----->terminalId: "+terminalId+"    apkChannelVersion: "+apkChannelVersion);
                    mSyncEs.submit(new ServerParamDownloadTask(new ISuccessCallback() {
                        @Override
                        public void onSuccess(HashMap<String, String> result) {
                            String body = result.get(IHttpEngine.KEY_BODY);
                            Log.d(TAG,"Server Param Download onSuccess---->body: "+body);
                            try{
                                ServerParamDownloadResponse response = new Gson().fromJson(body,ServerParamDownloadResponse.class);
                                Log.d(TAG,"Server Param Download onSuccess---->response: "+response.toString());

                                try {
                                    final int len = mRemoteCallbackList.beginBroadcast();
                                    Log.d(TAG,"Server Param Download onSuccess---->callback num: "+len+"   token: "+mToken);
                                    for (int i = 0; i < len; i++) {
                                        try {
                                            // 通知回调
                                            mRemoteCallbackList.getBroadcastItem(i).serverParamCallback(response);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    mRemoteCallbackList.finishBroadcast();
                                    Log.d(TAG,"Server Param Download onSuccess------finishBroadcast");
                                }

                                String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                                response(mToken,terminalId,productType,DeviceCommand.ServerParam.CODE,DeviceCommand.ServerParam.SUCCESS,"获取服务器参数成功");
                            }catch (Exception e){
                                e.printStackTrace();
                                String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                                response(mToken,terminalId,productType,DeviceCommand.ServerParam.CODE,DeviceCommand.ServerParam.FAILED,"获取服务器参数失败");
                            }
                        }
                    }, new IFailCallback() {
                        @Override
                        public void onFail(int errorCode, String errorStr) {
                            Log.e(TAG,"Server  errorCode: "+errorCode+"   errorStr: "+errorStr);
                            String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                            response(mToken,terminalId,productType,DeviceCommand.ServerParam.CODE,DeviceCommand.ServerParam.FAILED,"获取服务器参数失败");
                        }
                    }, mToken,terminalId,apkChannelVersion));
                    break;
                }
                case MSG_DOWNLOAD_TERMINAL_PARAM:{
                    final AdasPrefs prefs = Factory.get().getApplicationPrefs();
                    final String terminalId = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
                    String apkChannelVersion = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION_DEFAULT);
                    Log.d(TAG,"Msg download Terminal param----->terminalId: "+terminalId+"    apkChannelVersion: "+apkChannelVersion);
                    mSyncEs.submit(new TerminalParamDownloadTask(new ISuccessCallback() {
                        @Override
                        public void onSuccess(HashMap<String, String> result) {
                            String body = result.get(IHttpEngine.KEY_BODY);
                            Log.d(TAG,"Terminal Param Download onSuccess---->body: "+body);
                            try{
                                TerminalParamDownloadResponse response = new Gson().fromJson(body,TerminalParamDownloadResponse.class);
                                Log.d(TAG,"Terminal Param Download onSuccess---->response: "+response.toString());

                                try {
                                    final int len = mRemoteCallbackList.beginBroadcast();
                                    Log.d(TAG,"Terminal Param Download onSuccess---->callback num: "+len+"   token: "+mToken);
                                    for (int i = 0; i < len; i++) {
                                        try {
                                            // 通知回调
                                            mRemoteCallbackList.getBroadcastItem(i).terminalParamCallback(response);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    mRemoteCallbackList.finishBroadcast();
                                    Log.d(TAG,"Terminal Param Download onSuccess------finishBroadcast");
                                }

                                String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                                response(mToken,terminalId,productType,DeviceCommand.TermParam.CODE,DeviceCommand.TermParam.SUCCESS,"获取终端参数成功");
                            }catch (Exception e){
                                e.printStackTrace();
                                String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                                response(mToken,terminalId,productType,DeviceCommand.TermParam.CODE,DeviceCommand.TermParam.FAILED,"获取终端参数失败");
                            }
                        }
                    }, new IFailCallback() {
                        @Override
                        public void onFail(int errorCode, String errorStr) {
                            Log.e(TAG,"Terminal  errorCode: "+errorCode+"   errorStr: "+errorStr);
                            String productType = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE_DEFAULT);
                            response(mToken,terminalId,productType,DeviceCommand.TermParam.CODE,DeviceCommand.TermParam.FAILED,"获取终端参数失败");
                        }
                    }, mToken,terminalId,apkChannelVersion,0,0));
                    break;
                }
                case MSG_LOGIN_CONTINUE:{
                    Log.d(TAG,"Msg Login");
                    loginNotify.OnLogin();
                    break;
                }
            }
        }
    };

    private void response(String token,String deviceCode,String productType,String msgType,String msgCode,String msgContent){
        Log.d(TAG,"E: response");
        PutMsgRequest request = new PutMsgRequest(deviceCode,productType,msgType,msgCode,msgContent);
        String jsonRequest = new Gson().toJson(request);
        Log.d(TAG,"response----->jsonRequest: "+jsonRequest);
        Factory.get().getHttpEngine().OnPostRequest(UrlConstant.PUTMSG_URL,token,jsonRequest,null,null);
        Log.d(TAG,"X: response");
    }

    //Binder类
    class LocalBinder extends IAdasBinder.Stub {

        @Override
        public void registerCallback(IAdasCallback cb) throws RemoteException {
            Log.d(TAG, "registerCallback----->cb: "+cb);
            if (cb != null) {
                mRemoteCallbackList.register(cb);
            }
        }

        @Override
        public void unregisterCallback(IAdasCallback cb) throws RemoteException {
            Log.d(TAG, "unregisterCallback---->cb: "+cb);
            if (cb != null) {
                mRemoteCallbackList.unregister(cb);
            }
        }

        @Override
        public void OnLogin(LoginRequest loginRequest,String deviceVersion) throws RemoteException {
            Log.d(TAG, "OnLogin------>simNo: " + loginRequest.getSimNo() + "  terminalId: " + loginRequest.getTerminalId() + "  imei: " + loginRequest.getImei() + "  simType: " + loginRequest.getSimType() + "  macAddress: " + loginRequest.getMacAddress() + "  serialNo: " + loginRequest.getSerialNo() + "  productType: " + loginRequest.getProductType()+"   deviceVersion: "+deviceVersion);
            final AdasPrefs prefs = Factory.get().getApplicationPrefs();
            if (!TextUtils.isEmpty(loginRequest.getSimNo()) && (loginRequest.getSimNo().trim().length() == 11)) { //mcu唯一号
                prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MUCUNQIUE, loginRequest.getSimNo().trim());
                prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE, loginRequest.getSimNo().trim());
                prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SERIALNO, loginRequest.getSimNo().trim());
                prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO,loginRequest.getSimNo().trim());
            }else {
                prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MUCUNQIUE, "");
                prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_ICCID,loginRequest.getSimNo());

                String simNo = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMNO_DEFAULT);
                Log.d(TAG,"OnLogin----唯一号--->simNo: "+simNo);
                if(!TextUtils.isEmpty(simNo)){
                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,simNo);
                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SERIALNO,simNo);
                }else {
                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,loginRequest.getSimNo());
                    prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SERIALNO,loginRequest.getSimNo());
                }
            }
            prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_IMEI,loginRequest.getImei());
            prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_SIMTYPE,loginRequest.getSimType());
            prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAC,loginRequest.getMacAddress());
            prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_PRODUCTTYPE,loginRequest.getProductType());
            prefs.putString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICEVERSION,deviceVersion);
        }

        @Override
        public void OnSelfCheck(String token,int detectType,String topic,String deviceId,String cmdSNO,String command,SelfCheck selfCheck) throws RemoteException {
            final String pTopic = topic;
            final String pdeviceId = deviceId;
            final String pcmdSNO = cmdSNO;
            final String pcommand = command;
            final int pdetectType = detectType;
            if(detectType == 4){//指令自检
                selfCheck.setCommandId(cmdSNO);
            }
            Log.d(TAG, "OnSelfCheck----->topic: "+topic+"   deviceId: "+deviceId+"   cmdSNO: "+cmdSNO+"   command: "+command+"  detectType: "+detectType);
            Map<String, Map<String, String>> cameraMaps = selfCheck.getCamera();
            Iterator iters = cameraMaps.entrySet().iterator();
            while (iters.hasNext()) {
                Map.Entry entrys = (Map.Entry) iters.next();
                String keys = (String)entrys.getKey();
                Map<String, String> valmaps = (Map<String, String>)entrys.getValue();

                if("adasCamera".equalsIgnoreCase(keys)){
                    Iterator iter = valmaps.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String)entry.getKey();
                        if("cameraisload".equalsIgnoreCase(key)){
                            String val = (String)entry.getValue();
                            adasCameraState.setMount("0".equals(val));
                        }else if("camerastatus".equalsIgnoreCase(key)){
                            String val = (String)entry.getValue();
                            adasCameraState.setHasFrame("0".equals(val));
                        }
                    }
                }else if("dsmCamera".equalsIgnoreCase(keys)){
                    Iterator iter = valmaps.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String)entry.getKey();
                        if("cameraisload".equalsIgnoreCase(key)){
                            String val = (String)entry.getValue();
                            dsmCameraState.setMount("0".equals(val));
                        }else if("camerastatus".equalsIgnoreCase(key)){
                            String val = (String)entry.getValue();
                            dsmCameraState.setHasFrame("0".equals(val));
                        }
                    }
                }else if("camera3".equalsIgnoreCase(keys)){
                    Iterator iter = valmaps.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String)entry.getKey();
                        if("cameraisload".equalsIgnoreCase(key)){
                            String val = (String)entry.getValue();
                            h264CameraState.setMount("0".equals(val));
                        }else if("camerastatus".equalsIgnoreCase(key)){
                            String val = (String)entry.getValue();
                            h264CameraState.setHasFrame("0".equals(val));
                        }
                    }
                }
            }

            mSyncEs.submit(new SelfCheckTask(new ISuccessCallback() {
                @Override
                public void onSuccess(HashMap<String, String> result) {
                    String body = result.get(IHttpEngine.KEY_BODY);
                    Log.d(TAG,"Self Check onSuccess---->body: "+body);
                    try{
                        BaseResponse response = new Gson().fromJson(body,BaseResponse.class);
                        Log.d(TAG,"Self Check onSuccess---->"+response.toString());
                        if(pdetectType == 4){
                            MqttResponse mqttResponse = new MqttResponse();
                            mqttResponse.setDeviceId(pdeviceId);
                            mqttResponse.setCmdSNO(pcmdSNO);
                            mqttResponse.setCommand(pcommand);
                            MqttResponse.Response subesponse = new  MqttResponse.Response();
                            subesponse.setFlag(true);
                            subesponse.setMessage("指令自检执行成功");
                            subesponse.setData("");
                            mqttResponse.setResponse(subesponse);
                            mqttResponse.setState(DeviceCommand.MqttCmdState.CmdExecuted);
                            String jsonResponse = new Gson().toJson(mqttResponse);
                            Log.d(TAG,"mqtt Self Check onSuccess----->jsonRequest: "+jsonResponse);
                            mqttService.publish(pTopic,jsonResponse,0);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new IFailCallback() {
                @Override
                public void onFail(int errorCode, String errorStr) {
                    Log.e(TAG,"Self Check  errorCode: "+errorCode+"   errorStr: "+errorStr);
                    if(pdetectType == 4){
                        MqttResponse mqttResponse = new MqttResponse();
                        mqttResponse.setDeviceId(pdeviceId);
                        mqttResponse.setCmdSNO(pcmdSNO);
                        mqttResponse.setCommand(pcommand);
                        MqttResponse.Response subesponse = new  MqttResponse.Response();
                        subesponse.setFlag(true);
                        subesponse.setMessage("指令自检执行失败");
                        subesponse.setData("");
                        mqttResponse.setResponse(subesponse);
                        mqttResponse.setState(DeviceCommand.MqttCmdState.CmdExecuted);
                        String jsonResponse = new Gson().toJson(mqttResponse);
                        Log.d(TAG,"mqtt Self Check failed----->jsonRequest: "+jsonResponse);
                        mqttService.publish(pTopic,jsonResponse,0);
                    }
                }
            }, mToken, selfCheck));
        }

        @Override
        public void OnTakePicUpload(String topic,String deviceId,String cmdSNO,String command,int channel,String filePath) throws RemoteException{
            Log.d(TAG, "OnTakePicUpload----->topic: "+topic+",deviceId: "+deviceId+",cmdSNO: "+cmdSNO+",command: "+command+", channel: "+channel+", filePath: "+filePath);
            if(TextUtils.isEmpty(filePath)){
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response takePictureResponse = new  MqttResponse.Response();
                takePictureResponse.setFlag(true);
                if(channel == 100){
                    takePictureResponse.setMessage("车道拍照失败,文件未生成");
                }else if(channel == 101){
                    takePictureResponse.setMessage("人脸拍照失败,文件未生成");
                }else if(channel == 3){
                    takePictureResponse.setMessage("第三路拍照失败,文件未生成");
                }
                takePictureResponse.setData("");
                mqttResponse.setResponse(takePictureResponse);
                mqttResponse.setState(DeviceCommand.MqttTakePicCmd.FileFailed);
                String jsonResponse = new Gson().toJson(mqttResponse);
                Log.d(TAG,"OnTakePicUpload---->jsonRequest: "+jsonResponse);
                mqttService.publish(topic,jsonResponse,0);
                return;
            }
            final String pTopic = topic;
            final String pdeviceId = deviceId;
            final String pcmdSNO = cmdSNO;
            final String pcommand = command;
            final String pfilePath = filePath;
            final int pchannel = channel;
            mSyncEs.submit(new UploadFileTask(new ISuccessCallback() {
                @Override
                public void onSuccess(HashMap<String, String> result) {
                    String body = result.get(IHttpEngine.KEY_BODY);
                    Log.d(TAG,"OnTakePicUpload onSuccess---->body: "+body);
                    try{
                        DeviceUploadFileResponse response = new Gson().fromJson(body,DeviceUploadFileResponse.class);
                        Log.d(TAG,"OnTakePicUpload onSuccess---->countTakePicTotal: "+countTakePicTotal+"   takePicDoneNum: "+takePicDoneNum+"  response: "+response.toString());

                        MqttResponse mqttResponse = new MqttResponse();
                        mqttResponse.setDeviceId(pdeviceId);
                        mqttResponse.setCmdSNO(pcmdSNO);
                        mqttResponse.setCommand(pcommand);
                        MqttResponse.Response takePictureResponse = new  MqttResponse.Response();
                        takePictureResponse.setFlag(true);
                        if(pchannel == 100){
                            takePictureResponse.setMessage("车道拍照成功,文件上传成功");
                        }else if(pchannel == 101){
                            takePictureResponse.setMessage("人脸拍照成功,文件上传成功");
                        }else if(pchannel == 3){
                            takePictureResponse.setMessage("第三路拍照成功,文件上传成功");
                        }
                        takePictureResponse.setData("");
                        mqttResponse.setResponse(takePictureResponse);
                        mqttResponse.setState(DeviceCommand.MqttTakePicCmd.FileUploadSuccess);
                        String jsonResponse = new Gson().toJson(mqttResponse);
                        Log.d(TAG,"OnTakePicUpload----->jsonRequest: "+jsonResponse);
                        mqttService.publish(pTopic,jsonResponse,0);

                        if(pcommand.equalsIgnoreCase("heightAdjust") || pcommand.equalsIgnoreCase("ldwAdjust")){
                            PutUpFileInfo putUpFileInfo = new PutUpFileInfo();
                            PutUpFileInfo.Data[] fileList = new PutUpFileInfo.Data[1];
                            fileList[0] = new PutUpFileInfo.Data();
                            fileList[0].setCommandId(pcmdSNO);
                            fileList[0].setFileUrl(response.getData().getUrl());
                            String fileName = pfilePath.substring(pfilePath.lastIndexOf("/")+1);
                            fileList[0].setFileName(fileName);
                            fileList[0].setChannelNo(pchannel);
                            fileList[0].setFileType("1");
                            putUpFileInfo.setDeviceCode(pdeviceId);
                            putUpFileInfo.setFileList(fileList);

                            fileInfoUploadEs.submit(new PutUpFileInfoTask(new ISuccessCallback() {
                                @Override
                                public void onSuccess(HashMap<String, String> result) {
                                    String body = result.get(IHttpEngine.KEY_BODY);
                                    Log.d(TAG,"PutUpFileInfo onSuccess---->body: "+body);
                                    try{
                                        BaseResponse response = new Gson().fromJson(body,BaseResponse.class);
                                        Log.d(TAG,"PutUpFileInfo onSuccess---->"+response.toString());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            },new IFailCallback() {
                                @Override
                                public void onFail(int errorCode, String errorStr) {
                                    Log.e(TAG,"PutUpFileInfo  errorCode: "+errorCode+"   errorStr: "+errorStr);
                                }
                            },mToken,putUpFileInfo));
                        }else if(pcommand.equalsIgnoreCase("takePicture")) {
                            mFileList[takePicDoneNum] = new PutUpFileInfo.Data();
                            mFileList[takePicDoneNum].setCommandId(pcmdSNO);
                            mFileList[takePicDoneNum].setFileUrl(response.getData().getUrl());
                            String fileName = pfilePath.substring(pfilePath.lastIndexOf("/") + 1);
                            mFileList[takePicDoneNum].setFileName(fileName);
                            mFileList[takePicDoneNum].setChannelNo(pchannel);
                            mFileList[takePicDoneNum].setFileType("1");
                            mPutUpFileInfo.setDeviceCode(pdeviceId);
                            mPutUpFileInfo.setFileList(mFileList);
                            takePicDoneNum++;
                            if (takePicDoneNum == countTakePicTotal) {
                                fileInfoUploadEs.submit(new PutUpFileInfoTask(new ISuccessCallback() {
                                    @Override
                                    public void onSuccess(HashMap<String, String> result) {
                                        String body = result.get(IHttpEngine.KEY_BODY);
                                        Log.d(TAG, "PutUpFileInfo onSuccess---->body: " + body);
                                        try {
                                            BaseResponse response = new Gson().fromJson(body, BaseResponse.class);
                                            Log.d(TAG, "PutUpFileInfo onSuccess---->" + response.toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new IFailCallback() {
                                    @Override
                                    public void onFail(int errorCode, String errorStr) {
                                        Log.e(TAG, "PutUpFileInfo  errorCode: " + errorCode + "   errorStr: " + errorStr);
                                    }
                                }, mToken, mPutUpFileInfo));

                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new IFailCallback() {
                @Override
                public void onFail(int errorCode, String errorStr) {
                    Log.e(TAG,"OnTakePicUpload  errorCode: "+errorCode+"   errorStr: "+errorStr);

                    MqttResponse mqttResponse = new MqttResponse();
                    mqttResponse.setDeviceId(pdeviceId);
                    mqttResponse.setCmdSNO(pcmdSNO);
                    mqttResponse.setCommand(pcommand);
                    MqttResponse.Response takePictureResponse = new  MqttResponse.Response();
                    takePictureResponse.setFlag(true);
                    if(pchannel == 100){
                        takePictureResponse.setMessage("车道拍照成功,文件上传失败");
                    }else if(pchannel == 101){
                        takePictureResponse.setMessage("人脸拍照成功,文件上传失败");
                    }else if(pchannel == 3){
                        takePictureResponse.setMessage("第三路拍照成功,文件上传失败");
                    }
                    takePictureResponse.setData("");
                    mqttResponse.setResponse(takePictureResponse);
                    mqttResponse.setState(DeviceCommand.MqttTakePicCmd.FileUploadFailed);
                    String jsonResponse = new Gson().toJson(mqttResponse);
                    Log.d(TAG,"OnTakePicUpload------->jsonRequest: "+jsonResponse);
                    mqttService.publish(pTopic,jsonResponse,0);
                }
            }, mToken, filePath));
        }

        @Override
        public void OnCondTakePicUpload(String topic,String deviceId,String cmdSNO,String command,int channel,String batchNum,String filePath) throws RemoteException{
            Log.d(TAG, "OnCondTakePicUpload----->topic: "+topic+",deviceId: "+deviceId+",cmdSNO: "+cmdSNO+",command: "+command+", channel: "+channel+", batchNum: "+batchNum+", filePath: "+filePath);
            if(TextUtils.isEmpty(filePath)){
                MqttResponse mqttResponse = new MqttResponse();
                mqttResponse.setDeviceId(deviceId);
                mqttResponse.setCmdSNO(cmdSNO);
                mqttResponse.setCommand(command);
                MqttResponse.Response takePictureResponse = new  MqttResponse.Response();
                takePictureResponse.setFlag(true);
                if(channel == 100){
                    takePictureResponse.setMessage("车道拍照失败,文件未生成");
                }else if(channel == 101){
                    takePictureResponse.setMessage("人脸拍照失败,文件未生成");
                }else if(channel == 3){
                    takePictureResponse.setMessage("第三路拍照失败,文件未生成");
                }
                takePictureResponse.setData("");
                mqttResponse.setResponse(takePictureResponse);
                mqttResponse.setState(DeviceCommand.MqttTakePicCmd.FileFailed);
                String jsonResponse = new Gson().toJson(mqttResponse);
                Log.d(TAG,"OnCondTakePicUpload---->jsonRequest: "+jsonResponse);
                mqttService.publish(topic,jsonResponse,0);
                return;
            }
            final String pTopic = topic;
            final String pdeviceId = deviceId;
            final String pcmdSNO = cmdSNO;
            final String pcommand = command;
            final String pfilePath = filePath;
            final int pchannel = channel;
            mSyncEs.submit(new UploadFileTask(new ISuccessCallback() {
                @Override
                public void onSuccess(HashMap<String, String> result) {
                    String body = result.get(IHttpEngine.KEY_BODY);
                    Log.d(TAG,"OnCondTakePicUpload onSuccess---->body: "+body);
                    try{
                        DeviceUploadFileResponse response = new Gson().fromJson(body,DeviceUploadFileResponse.class);
                        Log.d(TAG,"OnCondTakePicUpload onSuccess---->response: "+response.toString());

                        MqttResponse mqttResponse = new MqttResponse();
                        mqttResponse.setDeviceId(pdeviceId);
                        mqttResponse.setCmdSNO(pcmdSNO);
                        mqttResponse.setCommand(pcommand);
                        MqttResponse.Response takePictureResponse = new  MqttResponse.Response();
                        takePictureResponse.setFlag(true);
                        if(pchannel == 100){
                            takePictureResponse.setMessage("车道拍照成功,文件上传成功");
                        }else if(pchannel == 101){
                            takePictureResponse.setMessage("人脸拍照成功,文件上传成功");
                        }else if(pchannel == 3){
                            takePictureResponse.setMessage("第三路拍照成功,文件上传成功");
                        }
                        takePictureResponse.setData("");
                        mqttResponse.setResponse(takePictureResponse);
                        mqttResponse.setState(DeviceCommand.MqttTakePicCmd.FileUploadSuccess);
                        String jsonResponse = new Gson().toJson(mqttResponse);
                        Log.d(TAG,"OnCondTakePicUpload----->jsonRequest: "+jsonResponse);
                        mqttService.publish(pTopic,jsonResponse,0);

                        PutUpFileInfo putUpFileInfo = new PutUpFileInfo();
                        PutUpFileInfo.Data[] fileList = new PutUpFileInfo.Data[1];
                        fileList[0] = new PutUpFileInfo.Data();
                        fileList[0].setCommandId(pcmdSNO);
                        fileList[0].setFileUrl(response.getData().getUrl());
                        String fileName = pfilePath.substring(pfilePath.lastIndexOf("/") + 1);
                        fileList[0].setFileName(fileName);
                        fileList[0].setChannelNo(pchannel);
                        fileList[0].setFileType("1");
                        putUpFileInfo.setDeviceCode(pdeviceId);
                        putUpFileInfo.setConnectorType("1");
                        putUpFileInfo.setFileList(fileList);

                        fileInfoUploadEs.submit(new PutUpFileInfoTask(new ISuccessCallback() {
                            @Override
                            public void onSuccess(HashMap<String, String> result) {
                                String body = result.get(IHttpEngine.KEY_BODY);
                                Log.d(TAG, "PutUpFileInfo onSuccess---->body: " + body);
                                try {
                                    BaseResponse response = new Gson().fromJson(body, BaseResponse.class);
                                    Log.d(TAG, "PutUpFileInfo onSuccess---->" + response.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new IFailCallback() {
                            @Override
                            public void onFail(int errorCode, String errorStr) {
                                Log.e(TAG, "PutUpFileInfo  errorCode: " + errorCode + "   errorStr: " + errorStr);
                            }
                        }, mToken, putUpFileInfo));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new IFailCallback() {
                @Override
                public void onFail(int errorCode, String errorStr) {
                    Log.e(TAG,"OnCondTakePicUpload  errorCode: "+errorCode+"   errorStr: "+errorStr);

                    MqttResponse mqttResponse = new MqttResponse();
                    mqttResponse.setDeviceId(pdeviceId);
                    mqttResponse.setCmdSNO(pcmdSNO);
                    mqttResponse.setCommand(pcommand);
                    MqttResponse.Response takePictureResponse = new  MqttResponse.Response();
                    takePictureResponse.setFlag(true);
                    if(pchannel == 100){
                        takePictureResponse.setMessage("车道拍照成功,文件上传失败");
                    }else if(pchannel == 101){
                        takePictureResponse.setMessage("人脸拍照成功,文件上传失败");
                    }else if(pchannel == 3){
                        takePictureResponse.setMessage("第三路拍照成功,文件上传失败");
                    }
                    takePictureResponse.setData("");
                    mqttResponse.setResponse(takePictureResponse);
                    mqttResponse.setState(DeviceCommand.MqttTakePicCmd.FileUploadFailed);
                    String jsonResponse = new Gson().toJson(mqttResponse);
                    Log.d(TAG,"OnCondTakePicUpload------->jsonRequest: "+jsonResponse);
                    mqttService.publish(pTopic,jsonResponse,0);
                }
            }, mToken, batchNum,filePath));
        }

        @Override
        public void OnParam(String topic,String deviceId,String cmdSNO,String command,String[] result){
            Log.d(TAG, "OnParam----->topic: "+topic+",deviceId: "+deviceId+",cmdSNO: "+cmdSNO+",command: "+command+", result: "+result);
            MqttParamResponse mqttResponse = new MqttParamResponse();
            mqttResponse.setDeviceId(deviceId);
            mqttResponse.setCmdSNO(cmdSNO);
            mqttResponse.setCommand(command);
            MqttParamResponse.Response response = new MqttParamResponse.Response();
            response.setFlag(true);
            MqttParamResponse.Data data = new MqttParamResponse.Data();
            if(result != null){
                response.setMessage(String.format("%s指令执行成功",command));
                data.setResults(result);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdExecuted);
            }else {
                response.setMessage(String.format("%s指令执行失败",command));
                data.setResults(null);
                mqttResponse.setState(DeviceCommand.MqttCmdState.CmdExecuted_Failed);
            }

            response.setData(data);
            mqttResponse.setResponse(response);
            String jsonResponse = new Gson().toJson(mqttResponse);
            Log.d(TAG,"OnParam------>jsonRequest: "+jsonResponse);
            mqttService.publish(topic,jsonResponse,0);
        }

        @Override
        public  void OnSmsParam(String phone,String[] result){
            StringBuffer msgBuffer = new StringBuffer();
            for(int i=0;i<result.length;i++){
                msgBuffer.append(result[i]);
            }
            String msg = msgBuffer.toString();
            Log.d(TAG, "OnSmsParam----->phone: "+phone+", msg: "+msg);
            SMSBroadcastReceiver.sendMessage(phone,msg);
        }

    }
}
