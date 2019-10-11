package com.openplatform.adas.constant;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/30 10:21
 * Description :
 */
public interface DeviceCommand {

    interface Upgrade {
        String CODE = "02";
        String DOWNLOAD_SUCCESS = "0201";
        String DOWNLOAD_FAILED = "0202";
        String UPGRADE_SUCCESS = "0203";
        String UPGRADE_FAILED = "0204";
        String DOWNLOAD_NOT_COMPLETED = "0205";

        interface UpgradeApp {
            String MAIN_APP = "MainApp";
        }
    }

    interface TermParam {
        String CODE = "03";
        String SUCCESS = "0301";
        String FAILED = "0302";
    }

    interface ServerParam {
        String CODE = "04";
        String SUCCESS = "0401";
        String FAILED = "0402";
    }

    interface MqttCmdState {
        int CmdReceived = 1; //指令已接收
        int CmdExecuted = 2; //指令已执行
        int CmdExecuted_Failed = 3;
    }

    interface MqttUpgradeCmdState {
        int CmdReceived = 1; //指令已接收
        int DOWNLOAD_SUCCESS = 2; //包已下载
        int DOWNLOAD_FAILED = 3; //包下载失败
        int UPGRADE_SUCCESS = 4;//升级成功
        int UPGRADE_FAILED = 5;//升级失败
        int DOWNLOAD_NOT_COMPLETED = 6;//下载未完成
        int ERROR = 7;//异常
    }

    interface MqttTermParamCmd {
        int CmdReceived = 1; //指令已接收
        int SUCCESS = 2; //终端参数获取成功
        int FAILED = 3; //终端参数获取失败
    }

    interface MqttServerParamCmd {
        int CmdReceived = 1; //指令已接收
        int SUCCESS = 2; //服务器参数获取成功
        int FAILED = 3; //服务器参数获取失败
    }

    interface MqttTakePicCmd {
        int CmdReceived = 1; //指令已接收
        int DateError = 2; //通道号数据格式错误
        int FileFailed = 3; //照片生成失败
        int FileUploadSuccess = 4; //照片上传成功
        int FileUploadFailed = 5; //上传失败
    }
}
