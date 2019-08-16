package com.openplatform.adas.util;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/29 10:21
 * Description :
 */
public final class OpenPlatformPrefsKeys {

    public interface AdasParamKey {
        String KEY_OPEN_TOKEN = "key_open_token";
        String KEY_OPEN_TOKEN_DEFAULT = "";

        String KEY_OPEN_ICCID = "key_open_iccid";
        String KEY_OPEN_ICCID_DEFAULT = "";

        String KEY_OPEN_SIMNO = "key_open_simno"; //终端唯一号
        String KEY_OPEN_SIMNO_DEFAULT = "";

        String KEY_OPEN_DEVICECODE = "key_open_devicecode"; //终端唯一号
        String KEY_OPEN_DEVICECODE_DEFAULT = "";

        String KEY_OPEN_IMEI = "key_open_imei"; //SIM卡唯一号
        String KEY_OPEN_IMEI_DEFAULT = "";

        String KEY_OPEN_SIMTYPE = "key_open_simtype"; //电信运营商
        String KEY_OPEN_SIMTYPE_DEFAULT = "0";

        String KEY_OPEN_MAC = "key_open_mac"; //MAC地址
        String KEY_OPEN_MAC_DEFAULT = "";

        String KEY_OPEN_SERIALNO = "key_open_serialno"; //终端序列号
        String KEY_OPEN_SERIALNO_DEFAULT = "";

        String KEY_OPEN_PRODUCTTYPE = "key_open_producttype"; //产品类型
        String KEY_OPEN_PRODUCTTYPE_DEFAULT = "";

        String KEY_OPEN_DEVICEVERSION = "key_open_deviceversion"; //渠道号
        String KEY_OPEN_DEVICEVERSION_DEFAULT = "";

        String KEY_OPEN_MAINAPP_VERSION = "key_open_mainapp_version"; //主应用版本
        long KEY_OPEN_MAINAPP_VERSION_DEFAULT = 1;

        String KEY_OPEN_MUCUNQIUE = "key_open_mucunqiue"; //MUC唯一号
        String KEY_OPEN_MUCUNQIUE_DEFAULT = "";
    }
}
