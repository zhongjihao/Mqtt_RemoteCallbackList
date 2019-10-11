package com.openplatform.adas.interfacemanager;

import com.openplatform.aidl.CmdMesage;

import java.util.List;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/9/12 10:21
 * Description :
 */
public interface IOnCmdMessageProc {
    void onSmsMessageProc(List<CmdMesage> list);
}
