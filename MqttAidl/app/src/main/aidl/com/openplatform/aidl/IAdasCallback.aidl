// IAdasCallback.aidl
package com.openplatform.aidl;

import com.openplatform.aidl.TerminalParamDownloadResponse;
import com.openplatform.aidl.ServerParamDownloadResponse;
import com.openplatform.aidl.CmdMesage;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/19 10:21
 * Description :
 */
interface IAdasCallback {

   //登录回调
   void loginCallback(String token,String simNo);

   //终端参数下载回调
   void terminalParamCallback(in TerminalParamDownloadResponse response);

   //服务参数下载回调
   void serverParamCallback(in ServerParamDownloadResponse response);

   //拍照
   void mqttTakePic(String topic,String deviceId,String cmdSNO,String command,int cameraId);

   //自检指令
   void mqttDeviceDetect(String topic,String deviceId,String cmdSNO,String command);

   //条件拍照
   void mqttCondTakePic(String topic,String deviceId,String cmdSNO,String command,String batchNum,int channelId,int interval,int count,String distance,String minSpeed,int angle);

   //mqtt参数指令
   void mqttParamCmd(String topic,String deviceId,String cmdSNO,String command,in List<CmdMesage> cmdList);

   //短信参数指令
   void smsParamCmd(String phone,in List<CmdMesage> cmdList);
}
