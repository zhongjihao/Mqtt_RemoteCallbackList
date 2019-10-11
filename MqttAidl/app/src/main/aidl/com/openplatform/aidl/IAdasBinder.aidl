// IAdasBinder.aidl
package com.openplatform.aidl;

import com.openplatform.aidl.IAdasCallback;
import com.openplatform.aidl.SelfCheck;
import com.openplatform.aidl.LoginRequest;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/19 10:21
 * Description :
 */
interface IAdasBinder {

    void registerCallback(IAdasCallback cb);

    void unregisterCallback(IAdasCallback cb);

    //终端登录
    void OnLogin(in LoginRequest loginRequest,String deviceVersion);

    //终端自检信息上报
    void OnSelfCheck(String token,int detectType,String topic,String deviceId,String cmdSNO,String command,in SelfCheck selfCheck);

    //拍照上传
    void OnTakePicUpload(String topic,String deviceId,String cmdSNO,String command,int channel,String filePath);

    //条件拍照上传
    void OnCondTakePicUpload(String topic,String deviceId,String cmdSNO,String command,int channel,String batchNum,String filePath);

    //mqtt指令操作参数
    void OnParam(String topic,String deviceId,String cmdSNO,String command,in String[] result);

    //短信指令操作参数
    void OnSmsParam(String phone,in String[] result);
}
