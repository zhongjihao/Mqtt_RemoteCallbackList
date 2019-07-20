package com.openplatform.adas.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.openplatform.adas.Factory;
import com.openplatform.adas.manager.ExecCmdManager;
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

    private MqttAndroidClient mClient;
    private MqttConnectOptions mConOpt;

    private String userName = "admin";
    private String passWord = "T68iegprs";
    private String mTopic;      //要订阅的主题
    private Thread execThread;
    private volatile boolean isRunning;
    private AtomicBoolean mExecStatus;
    private LinkedBlockingQueue<ExecCmdManager> queue;

    @Override
    public void onCreate() {
        super.onCreate();
        final AdasPrefs prefs = Factory.get().getApplicationPrefs();
        mTopic = prefs.getString(OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE,OpenPlatformPrefsKeys.AdasParamKey.KEY_OPEN_DEVICECODE_DEFAULT);
        Log.d(TAG, "E: onCreate----->mTopic: "+mTopic);
        queue = new LinkedBlockingQueue<>();
        mExecStatus = new AtomicBoolean(true);
        execThread = new Thread(){
            @Override
            public void run() {
                while (isRunning){
                    try {
                        if(!queue.isEmpty() && mExecStatus.get()){
                            Log.d(TAG,"queue count: "+queue.size());
                            ExecCmdManager execCmdManager = queue.take();
                            Log.d(TAG,"ExecCmdManager pop queue, start process event");
                            execCmdManager.processEvent(mExecStatus);
                            mExecStatus.set(true);
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
        try {
            mClient.unregisterResources();
            mClient.disconnect();
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
        // 清除缓存
        mConOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        mConOpt.setConnectionTimeout(30);
        // 心跳包发送间隔，单位：秒
        mConOpt.setKeepAliveInterval(20);
        // 用户名
        mConOpt.setUserName(userName);
        // 密码
        mConOpt.setPassword(passWord.toCharArray());     //将字符串转换为字符串数组

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
            }
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
            arg1.printStackTrace();
            // 连接失败，重连
            Log.e(TAG, "onFailure");
            doClientConnection();
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
            Log.d(TAG,"connectionLost");
            doClientConnection();
        }
    };

}
