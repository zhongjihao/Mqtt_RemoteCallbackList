package com.openplatform.adas.manager;


import com.openplatform.adas.datamodel.MqttParamResponse;
import com.openplatform.adas.datamodel.MqttResponse;
import com.openplatform.adas.interfacemanager.INotifyCallback;
import com.openplatform.aidl.CmdMesage;
import com.openplatform.aidl.ServerParamDownloadResponse;
import com.openplatform.aidl.TerminalParamDownloadResponse;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/30 10:21
 * Description :
 */
public class NotifyManager {
    private static NotifyManager instance;
    private CopyOnWriteArrayList<INotifyCallback> notifyListeners = new CopyOnWriteArrayList<>();

    public static synchronized NotifyManager getInstance(){
        if(instance == null){
            instance = new NotifyManager();
        }
        return instance;
    }

    private NotifyManager(){

    }

    public void addNotifyListener(INotifyCallback listener){
        if(!notifyListeners.contains(listener)){
            notifyListeners.add(listener);
        }
    }

    public void removeNotifyListener(INotifyCallback listener){
        if(notifyListeners.contains(listener)){
            notifyListeners.remove(listener);
        }
    }

    public void OnTerminalParamNotify(TerminalParamDownloadResponse response) {
        for (INotifyCallback listener : notifyListeners) {
            listener.terminalParamCallback(response);
        }
    }

    public void OnServerParamNotify(ServerParamDownloadResponse response) {
        for (INotifyCallback listener : notifyListeners) {
            listener.serverParamCallback(response);
        }
    }

    public void OnMqttTakePicNotify(String topic,MqttResponse mqttResponse, String[] cameraIds) {
        for (INotifyCallback listener : notifyListeners) {
            listener.mqttTakePicEvent(topic,mqttResponse,cameraIds);
        }
    }

    public void OnMqttSimpleCmdNotify(String topic,MqttResponse mqttResponse) {
        for (INotifyCallback listener : notifyListeners) {
            listener.mqttSimpleCmdEvent(topic,mqttResponse);
        }
    }

    public void OnMqttTakePicNotify(String topic,MqttResponse mqttResponse, String batchNum,String channelId,int interval,int count,String distance,String minSpeed,int angle) {
        for (INotifyCallback listener : notifyListeners) {
            listener.mqttTakePicEvent(topic,mqttResponse, batchNum, channelId, interval, count, distance, minSpeed, angle);
        }
    }

    public void OnMqttParamCmdNotify(String topic, MqttParamResponse mqttResponse, List<CmdMesage> list) {
        for (INotifyCallback listener : notifyListeners) {
            listener.mqttParamCmdEvent(topic,mqttResponse,list);
        }
    }

    public void OnSmsCmdNotify(String phone,List<CmdMesage> list) {
        for (INotifyCallback listener : notifyListeners) {
            listener.smsParamCmdEvent(phone,list);
        }
    }
}
