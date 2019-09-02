package com.openplatform.adas.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;


import com.openplatform.adas.Factory;
import com.openplatform.adas.constant.DeviceCommand;
import com.openplatform.adas.datamodel.Command;
import com.openplatform.adas.datamodel.MqttResponse;
import com.openplatform.adas.datamodel.UpdateItem;
import com.openplatform.adas.manager.ExecCmdManager;
import com.openplatform.adas.manager.UpgradeManager;
import com.openplatform.adas.util.AdasPrefs;
import com.openplatform.adas.util.NetUtil;
import com.openplatform.adas.util.OpenPlatformPrefsKeys;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/5/28 10:21
 * Description :
 */
public class MQTTService extends Service {
    private final static String TAG = "MQTTService";
    private final static int MSG_RECONNECT = 100;
    private final static int MSG_UPGRADE_CHECK = 101;

    private MqttAndroidClient mClient;
    private MqttConnectOptions mConOpt;

    private String userName = "admin";
    private String passWord = "T68iegprs";
    private String mTopic;      //要订阅的主题
    private Thread execThread;
    private volatile boolean isRunning;
    private AtomicBoolean mExecStatus;
    private LinkedBlockingQueue<ExecCmdManager> queue;
    private HandlerThread mHandlerThread;
    private Handler mHandler;


    @Override
    public void onCreate() {
        super.onCreate();
        final AdasPrefs prefs = Factory.get().getApplicationPrefs();
        mTopic = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
        Log.d(TAG, "E: onCreate----->mTopic: "+mTopic);
        queue = new LinkedBlockingQueue<>();
        mExecStatus = new AtomicBoolean(true);
        mHandlerThread = new HandlerThread( "MqttService-thread");
        //开启一个线程
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(),mCallback);

        execThread = new Thread(){
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (isRunning){
                    try {
                        if(!queue.isEmpty() && mExecStatus.get()){
                            Log.d(TAG,"queue count: "+queue.size());
                            ExecCmdManager execCmdManager = queue.take();
                            Log.d(TAG,"ExecCmdManager pop queue, start process event");
                            execCmdManager.processEvent(mExecStatus);
                            if(!TextUtils.isEmpty(execCmdManager.getCommand())){
                                if(execCmdManager.getCommand().equalsIgnoreCase("upgradeCmd")){
                                    Message message = Message.obtain();
                                    Log.d(TAG,"upgrade command rowId: "+execCmdManager.getRowId());
                                    if(execCmdManager.getRowId() > 0){
                                        message.obj = execCmdManager.getRowId();
                                        message.what = MSG_UPGRADE_CHECK;
                                        mHandler.sendMessageDelayed(message,20*60*1000); //延时20分钟
                                    }
                                }else {
                                    mExecStatus.set(true);
                                }
                            }else {
                                mExecStatus.set(true);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        queue.clear();
                        isRunning = true;
                        mExecStatus.set(true);
                        execThread.start();
                        break;
                    }
                }
            }
        };
        isRunning = true;
        execThread.start();
        Log.d(TAG, "X: onCreate");
    }

    public class MqttBinder extends Binder {
        public MQTTService getService(){
            return MQTTService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new MqttBinder();
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
        Log.d(TAG, "E: onDestroy");
        isRunning = false;
        queue.clear();
        if(mHandlerThread != null){
            mHandlerThread.quitSafely();
        }

        try {
            if(mClient != null){
                mClient.unregisterResources();
                mClient.disconnect();
            }
            Log.d(TAG, "X: onDestroy");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String msg, int qos) {
        Boolean retained = false;
        Log.d(TAG, "E: publish------>topic: " + topic + "  msg: " + msg);
        try {
            if (mClient != null) {
                mClient.publish(topic, msg.getBytes(), qos, retained.booleanValue());
                Log.d(TAG, "X: publish------>topic: " + topic + "  msg: " + msg);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connectServer(String ip,int port) {
        Log.d(TAG, "connectServer----->ip: "+ip+"  port: "+port+"  mTopic: "+mTopic);
        // 服务器地址（协议+地址+端口号）
        String uri = "tcp://"+ip+":"+port;

        mClient = new MqttAndroidClient(this, uri, mTopic);
        // 设置MQTT监听并且接受消息
        mClient.setCallback(mqttCallback);

        mConOpt = new MqttConnectOptions();
        //客户端掉线后 服务器端不会清除session，当重连后可以接收之前订阅主题的消息。当客户端上线后会接受到它离线的这段时间的消息
        mConOpt.setCleanSession(false);
        // 设置超时时间，单位：秒
        mConOpt.setConnectionTimeout(30);
        // 心跳包发送间隔，单位：秒
        mConOpt.setKeepAliveInterval(20);
        // 用户名
        mConOpt.setUserName(userName);
        // 密码
        mConOpt.setPassword(passWord.toCharArray());     //将字符串转换为字符串数组
        mConOpt.setAutomaticReconnect(true);

        // last will message
        boolean doConnect = true;
        String message = "{\"topic\":\"" + mTopic + "\"}";
        Log.d(TAG, "connectServer------>message:" + message);
        Integer qos = 0;
        Boolean retained = false;
        if (!TextUtils.isEmpty(message) || !TextUtils.isEmpty(mTopic)) {
            // 最后的遗嘱
            // MQTT本身就是为信号不稳定的网络设计的，所以难免一些客户端会无故的和Broker断开连接。
            //当客户端连接到Broker时，可以指定LWT，Broker会定期检测客户端是否有异常。
            //当客户端异常掉线时，Broker就往连接时指定的topic里推送当时指定的LWT消息。
            try {
                mConOpt.setWill(mTopic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception Occured: ", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            doClientConnection();
        }
    }

    /** 连接MQTT服务器 */
    private void doClientConnection() {
        boolean isNetOk = NetUtil.isNetworkAvailable(Factory.get().getApplicationContext());
        Log.d(TAG, "E: doClientConnection------>isNetOk: "+isNetOk);
        if(isNetOk){
            if (mClient != null && !mClient.isConnected()) {
                try {
                    Log.d(TAG, "doClientConnection" );
                    mClient.connect(mConOpt, null, iMqttActionListener);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }else {
                Log.d(TAG, "mqtt already connect" );
            }
        }else {
            mHandler.sendEmptyMessageDelayed(MSG_RECONNECT,60*1000);
        }
        Log.d(TAG, "X: doClientConnection" );
    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.d(TAG, "onSuccess");
            try {
                // 订阅mTopic话题
                mClient.subscribe(mTopic,1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            if(arg1 != null){
                arg1.printStackTrace();
                Log.e(TAG, "onFailure----Throwable: "+arg1.toString());
            }
            if(arg0 != null){
                arg0.getException().printStackTrace();
                Log.e(TAG, "onFailure---IMqttToken: "+arg0.getException().toString());
            }

            // 连接失败，重连
            mHandler.sendEmptyMessageDelayed(MSG_RECONNECT,60*1000);
        }
    };

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message){
            String msg = new String(message.getPayload());
            String info = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Log.d(TAG, "messageArrived----->msg: " + msg);
            Log.d(TAG, "messageArrived----->info: "+info);

            try {
                if (!TextUtils.isEmpty(msg)) {
                    Log.d(TAG,"messageArrived----put cmd ------->execStatus: "+mExecStatus.get());
                    queue.put(new ExecCmdManager(msg));
                }
            } catch (Exception e) {
                e.printStackTrace();
                queue.clear();
                mExecStatus.set(true);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.d(TAG,"deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
            if(arg0 != null){
                Log.e(TAG,"connectionLost: "+arg0.toString());
                arg0.printStackTrace();
            }
            doClientConnection();
        }
    };

    private Handler.Callback mCallback = new Handler.Callback() {
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_RECONNECT:{
                    Log.d(TAG, "mqtt reconnect msg");
                    if(mHandler.hasMessages(MSG_RECONNECT)){
                        mHandler.removeMessages(MSG_RECONNECT);
                    }
                    doClientConnection();
                    break;
                }
                case MSG_UPGRADE_CHECK:{
                    if(mHandler.hasMessages(MSG_UPGRADE_CHECK)){
                        mHandler.removeMessages(MSG_UPGRADE_CHECK);
                    }

                    boolean isAvail = NetUtil.isNetworkAvailable(Factory.get().getApplicationContext());
                    Log.d(TAG, "upgrade check msg  isAvail: "+isAvail);
                    if(msg.obj != null) {
                        final long rowId = (long) msg.obj;
                        Log.d(TAG, "upgrade check msg  isAvail: " + isAvail + "  rowId: " + rowId);

                        final Command command = Factory.get().getDbManager().queryCommand(rowId);
                        if (command != null) {
                            Log.d(TAG, "command: " + command.toString());
                            int count = command.getCount();
                            if (count < 3) {
                                if (command.getStatus() != DeviceCommand.MqttUpgradeCmdState.UPGRADE_SUCCESS) {
                                    if (isAvail) {
                                        Factory.get().getDbManager().updateCmdCount(rowId, ++count);

                                        final MqttResponse mqttResponse = new MqttResponse();
                                        mqttResponse.setDeviceId(command.getDeviceId());
                                        mqttResponse.setCmdSNO(command.getCmdSNO());
                                        mqttResponse.setCommand(command.getCommand());
                                        MqttResponse.Response response = new MqttResponse.Response();
                                        response.setFlag(true);
                                        response.setMessage("收到升级指令");
                                        response.setData("");
                                        mqttResponse.setResponse(response);
                                        mqttResponse.setState(DeviceCommand.MqttUpgradeCmdState.CmdReceived);

                                        Log.d(TAG, "handleMessage---upgrade---->rowId: " + rowId);

                                        UpdateItem item = Factory.get().getDbManager().queryUpgradeInfo(rowId);
                                        if(item != null){
                                            if (DeviceCommand.Upgrade.UpgradeApp.MAIN_APP.equalsIgnoreCase(item.getApkType())) {//主应用Adas
                                                long versionCode = Factory.get().getApplicationPrefs().getLong(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAINAPP_VERSION, OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_MAINAPP_VERSION_DEFAULT);
                                                Log.d(TAG, "handleMessage---OnDownUpgradeInfo-----upgrade main app, remote version: " + item.getVersion() + "  local version: " + versionCode + "  rowId: " + rowId);
                                                if (versionCode != item.getVersion()) {
                                                    UpgradeManager.getInstance().download(rowId, item, command.getCommand(), mqttResponse);
                                                }
                                            }
                                        }else {
                                            Log.e(TAG,"query upgrade is null ,commandId: "+rowId);
                                        }

                                        Message message = Message.obtain();
                                        message.obj = rowId;
                                        message.what = MSG_UPGRADE_CHECK;
                                        mHandler.sendMessageDelayed(message, 20 * 60 * 1000);
                                    } else {
                                        Message message = Message.obtain();
                                        message.obj = rowId;
                                        message.what = MSG_UPGRADE_CHECK;
                                        mHandler.sendMessageDelayed(message, 5 * 60 * 1000);
                                    }
                                }
                            } else {
                                Factory.get().getDbManager().deleteCommand(rowId);
                            }
                        } else {
                            Log.e(TAG, "command is null");
                        }
                    }
                    break;
                }
            }
            return true;
        }
    };

}
