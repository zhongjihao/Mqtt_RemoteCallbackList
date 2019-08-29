package com.openplatform.adas.interfacemanager;

import com.openplatform.adas.datamodel.MqttResponse;
import com.openplatform.aidl.ServerParamDownloadResponse;
import com.openplatform.aidl.TerminalParamDownloadResponse;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/30 10:21
 * Description :
 */
public interface INotifyCallback {
    void terminalParamCallback(TerminalParamDownloadResponse response);
    void serverParamCallback(ServerParamDownloadResponse response);
    void mqttTakePicEvent(String topic,MqttResponse mqttResponse, String[] cameraIds);
    void mqttSimpleCmdEvent(String topic,MqttResponse mqttResponse);
    void mqttTakePicEvent(String topic,MqttResponse mqttResponse,String batchNum,String channelId,int interval,int count,String distance,String minSpeed,int angle);
}
